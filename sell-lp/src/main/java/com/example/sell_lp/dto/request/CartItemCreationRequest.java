package com.example.sell_lp.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCreationRequest {

    private Integer quantity;
    private Integer cartId;
    private Long variantId;
}
