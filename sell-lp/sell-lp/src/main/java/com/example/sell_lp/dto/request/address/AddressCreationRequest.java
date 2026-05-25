package com.example.sell_lp.dto.request.address;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AddressCreationRequest {

    String recipientName;
    String phone;
    String street;
    String ward;
    String district;
    String city;
    String fullAddress;
    Double lat;
    Double lng;
    Boolean isDefault;


}
