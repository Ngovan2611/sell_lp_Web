package com.example.sell_lp.mapper;


import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrder(OrderCreationRequest orderCreationRequest);

    OrderResponse toOrderResponse(Order order);

}
