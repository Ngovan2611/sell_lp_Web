package com.example.sell_lp.dto.response;

import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductResponse {

    int productId;
    String name;
    String description;


    boolean isActive;

    @OneToMany(mappedBy = "product")
    List<ProductVariantResponse> variants;


    CategoryResponse category;

    List<String> images;
    String imageUrl;
    public Double getMinPrice() {
        if (variants == null || variants.isEmpty()) return 0.0;
        return variants.stream()
                .map(ProductVariantResponse::getPrice)
                .min(Double::compare)
                .orElse(0.0);
    }
    public int getTotalStock() {
        if (variants == null) return 0;
        return variants.stream()
                .mapToInt(ProductVariantResponse::getStockQty)
                .sum();
    }
}

