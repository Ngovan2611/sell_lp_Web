package com.example.sell_lp.dto.request;


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

    private String recipientName;
    private String phone;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String fullAddress;
    private Double lat;
    private Double lng;
    private Boolean isDefault;


}
