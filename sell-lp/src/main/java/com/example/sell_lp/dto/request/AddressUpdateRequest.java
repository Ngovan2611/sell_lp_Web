package com.example.sell_lp.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressUpdateRequest {
    private Long addressId;
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
