package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.OrderItemCreationRequest;
import com.example.sell_lp.entity.OrderItem;
import com.example.sell_lp.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    @Mapping(target = "variant", source = "variantId")
    OrderItem toEntity(OrderItemCreationRequest req);



    default ProductVariant map(Long variantId) {
        if (variantId == null) return null;

        ProductVariant v = new ProductVariant();
        v.setVariantId(variantId);
        return v;
    }


}