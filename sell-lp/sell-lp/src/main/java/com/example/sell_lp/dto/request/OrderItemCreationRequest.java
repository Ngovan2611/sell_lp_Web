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

public class OrderItemCreationRequest {
    Integer quantity;
    Double unitPrice;
    String productName;
    String variantName;
    String imageUrl;
    Integer orderId;
    Long variantId;
}
