package com.example.sell_lp.dto.request.product;

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
    Integer productId;
    String name;
    String description;
    Integer categoryId;
    boolean active;

    List<String> imageUrls;

    List<ProductVariantUpdateRequest> variants;
    List<TagRequest> tagIds;
}
