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

public class ProductVariantRequest{
    Integer colorId;
    Integer ramId;
    Integer romId;
    Double price;
    Integer stockQty;
}
