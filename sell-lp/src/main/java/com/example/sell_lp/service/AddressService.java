package com.example.sell_lp.service;

import com.example.sell_lp.dto.request.AddressUpdateRequest;
import com.example.sell_lp.dto.response.AddressResponse;
import com.example.sell_lp.entity.Address;
import com.example.sell_lp.dto.request.AddressCreationRequest;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.mapper.AddressMapper;
import com.example.sell_lp.repository.AddressRepository;
import com.example.sell_lp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeoCodingService geoCodingService;

    public AddressResponse saveAddress(AddressCreationRequest request) {
        String fullAddress = String.join(", ", request.getStreet(),
                request.getWard(), request.getDistrict(), request.getCity());
        request.setFullAddress(fullAddress);

        Map<String, Object> geo = geoCodingService.getCoordinatesFromAddress(fullAddress);
        Double lat = null;
        Double lng = null;
        if(geo != null) {
            lat = Double.parseDouble((String) geo.get("lat"));
            lng = Double.parseDouble((String) geo.get("lon"));
        }

        User user = userRepository.findByUsername(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        Address address = addressMapper.toAddress(request);
        address.setUser(user);
        address.setLat(lat);
        address.setLng(lng);

        addressRepository.save(address);
        return addressMapper.toAddressResponse(address);
    }
    public List<AddressResponse> getAllAddressesByUsername(String username) {
        User user = userRepository.findByUsername(username);

        List<Address> addresses = addressRepository.findAddressByUser(user);

        return addresses.stream().map(addressMapper::toAddressResponse).toList();

    }


    public void updateAddress(AddressUpdateRequest request, String username) {
        User user = userRepository.findByUsername(username);

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Not authorized to edit this address");
        }
        addressMapper.toAddressUpdateRequest(address, request);
        address.setFullAddress(String.join(", ",
                address.getCity(),
                address.getDistrict(),
                address.getWard(),
                address.getStreet()));
        addressRepository.save(address);
    }
}
