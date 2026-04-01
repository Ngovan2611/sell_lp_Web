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
public class ProductUpdateRequest {
    private Integer productId;
    private String name;
    private String description;
    private Integer categoryId;
    private boolean active;

    private List<String> imageUrls;

    private List<ProductVariantUpdateRequest> variants;
}
