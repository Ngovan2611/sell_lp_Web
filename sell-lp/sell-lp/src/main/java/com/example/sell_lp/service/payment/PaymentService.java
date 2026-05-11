package com.example.sell_lp.service.payment;

import com.example.sell_lp.dto.request.PaymentRequest;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.Payment;
import com.example.sell_lp.enums.PaymentMethod;
import com.example.sell_lp.enums.PaymentStatus;
import com.example.sell_lp.mapper.PaymentMapper;
import com.example.sell_lp.repository.order.OrderRepository;
import com.example.sell_lp.repository.payment.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {

    PaymentRepository paymentRepository;
    OrderRepository orderRepository;
    PaymentMapper paymentMapper;

    @Transactional
    public void createPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        Payment payment = paymentMapper.toEntity(request);

        if (request.getMethod() == PaymentMethod.VN_PAY) {
            if ("00".equals(request.getResponseCode())) {
                payment.setStatus(PaymentStatus.SUCCESS.name());
                payment.setPaidAt(LocalDateTime.now());
            } else {
                payment.setStatus(PaymentStatus.FAILED.name());
            }
        }

        payment.setOrder(order);
        payment = paymentRepository.save(payment);

        paymentMapper.toResponse(payment);
    }
}