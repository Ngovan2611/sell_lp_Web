package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.payment.PaymentRequest;
import com.example.sell_lp.dto.response.payment.PaymentResponse;
import com.example.sell_lp.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "order", ignore = true) // Sẽ set thủ công trong Service
    Payment toEntity(PaymentRequest request);

    @Mapping(source = "order.orderId", target = "orderId")
    PaymentResponse toResponse(Payment payment);
}