package com.example.sell_lp.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationRequest {

    private Long addressId;

    private String ids; // list cart item ids (VD: "1,2,3")

    private String paymentMethod;
}