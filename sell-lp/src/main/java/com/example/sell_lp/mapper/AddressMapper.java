package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.AddressCreationRequest;
import com.example.sell_lp.dto.request.AddressUpdateRequest;
import com.example.sell_lp.dto.request.UserUpdateRequest;
import com.example.sell_lp.dto.response.AddressResponse;
import com.example.sell_lp.entity.User;
import org.mapstruct.Mapper;
import com.example.sell_lp.entity.Address;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressCreationRequest addressCreationRequest);

    AddressResponse toAddressResponse(Address address);

    void toAddressUpdateRequest(@MappingTarget Address address, AddressUpdateRequest addressUpdateRequest);

}
