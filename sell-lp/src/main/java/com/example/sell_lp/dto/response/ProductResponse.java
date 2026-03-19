package com.example.sell_lp.dto.response;

import com.example.sell_lp.entity.Category;
import com.example.sell_lp.entity.ProductImage;
import com.example.sell_lp.entity.ProductVariant;

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
    List<ProductVariant> variants;


    Category category;

    List<ProductImage> images;
    String imageUrl;
}

