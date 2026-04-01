package com.example.sell_lp.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Data
public class ProductVariantUpdateRequest {
    private Long variantId;
    private Double price;
    private Integer stockQty;

}