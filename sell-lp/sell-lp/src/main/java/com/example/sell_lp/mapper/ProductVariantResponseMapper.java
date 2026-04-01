package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.ProductVariantRequest;
import com.example.sell_lp.dto.response.ProductVariantResponse;
import com.example.sell_lp.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductVariantResponseMapper {

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "color.colorName", target = "colorName")
    @Mapping(source = "ram.ramSize", target = "ramSize")
    @Mapping(source = "rom.romSize", target = "romSize")
    ProductVariantResponse toProductVariantResponse(ProductVariant variant);

    @Mapping(target = "variantId", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductVariant toProductVariant(ProductVariantRequest request);

}
