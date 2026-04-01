package com.example.sell_lp.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Data
public class ProductRequest {
    String name;
    Integer categoryId;
    String description;
    String imageUrl;

    List<ProductVariantRequest> variants;
}

