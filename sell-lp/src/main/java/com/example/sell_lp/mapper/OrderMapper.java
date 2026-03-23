package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.dto.response.OrderItemResponse;
import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toOrderResponse(Order order);
    OrderItemResponse toOrderItemResponse(OrderItem item);


}
