package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.OrderUpdateStatusRequest;
import com.example.sell_lp.dto.response.OrderItemResponse;
import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.OrderItem;
import com.example.sell_lp.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toOrderResponse(Order order);


    @Mapping(target = "variantName",
            expression = "java(mapVariantName(item.getVariant()))")    OrderItemResponse toOrderItemResponse(OrderItem item);

    default String mapVariantName(ProductVariant variant) {
        if (variant == null) return "";

        StringBuilder sb = new StringBuilder();

        if (variant.getRam() != null) {
            sb.append(variant.getRam().getRamSize()).append("GB RAM");
        }

        if (variant.getRom() != null) {
            if (!sb.isEmpty()) sb.append(" / ");
            sb.append(variant.getRom().getRomSize()).append("GB SSD");
        }

        if (variant.getColor() != null) {
            if (!sb.isEmpty()) sb.append(" / ");
            sb.append(variant.getColor().getColorName());
        }

        return sb.toString();
    }

}
