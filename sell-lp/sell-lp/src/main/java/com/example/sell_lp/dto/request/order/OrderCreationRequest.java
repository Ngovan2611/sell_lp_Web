package com.example.sell_lp.dto.request.order;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationRequest {

    private Long addressId;

    private String ids;

    private String paymentMethod;
}
