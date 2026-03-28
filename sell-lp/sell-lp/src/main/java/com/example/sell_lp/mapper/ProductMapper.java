package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(
            target = "imageUrl",
            expression = "java(product.getImages().stream()" +
                    ".filter(img -> img.isPrimary())" +
                    ".findFirst()" +
                    ".map(img -> img.getUrl())" +
                    ".orElse(null))"
    )
    ProductResponse productToProductResponse(Product product);
}
