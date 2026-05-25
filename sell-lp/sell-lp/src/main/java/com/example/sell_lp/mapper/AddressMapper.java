package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.address.AddressCreationRequest;
import com.example.sell_lp.dto.request.address.AddressUpdateRequest;
import com.example.sell_lp.dto.response.address.AddressResponse;
import org.mapstruct.Mapper;
import com.example.sell_lp.entity.Address;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressCreationRequest addressCreationRequest);

    AddressResponse toAddressResponse(Address address);

    void toAddressUpdateRequest(@MappingTarget Address address, AddressUpdateRequest addressUpdateRequest);

}
