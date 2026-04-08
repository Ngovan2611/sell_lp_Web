package com.example.sell_lp.dto.response;




import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductVariantResponse {
    Long variantId;
    Long productId;
    String productName;
    String colorName;
    Integer ramSize;
    Integer romSize;
    Integer stockQty;
    double price;
    String imageUrl;
}
