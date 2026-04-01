package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.ProductRequest;
import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.entity.Product;
import com.example.sell_lp.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductVariantResponseMapper.class})
public interface ProductMapper {

    @Mapping(
            target = "imageUrl",
            expression = "java(product.getImages() != null ? product.getImages().stream()" +
                    ".filter(img -> img.isPrimary())" +
                    ".findFirst()" +
                    ".map(img -> img.getUrl())" +
                    ".orElse(null) : null)"
    )
    @Mapping(target = "images", source = "images")
    @Mapping(target = "variants", source = "variants")
    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "variants", source = "variants")
    @Mapping(target = "category", ignore = true)
    Product toProduct(ProductRequest request);

    default List<String> mapImages(List<ProductImage> images) {
        if (images == null) return null;
        return images.stream()
                .map(ProductImage::getUrl)
                .toList();
    }

}