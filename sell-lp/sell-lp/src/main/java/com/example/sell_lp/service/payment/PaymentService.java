package com.example.sell_lp.service.payment;

import com.example.sell_lp.dto.request.PaymentRequest;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.Payment;
import com.example.sell_lp.enums.PaymentStatus;
import com.example.sell_lp.mapper.PaymentMapper;
import com.example.sell_lp.repository.OrderRepository;
import com.example.sell_lp.repository.PaymentRepository;
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

        payment.setOrder(order);
        if (PaymentStatus.SUCCESS.name().equals(request.getStatus()) || "00".equals(request.getResponseCode())) {
            payment.setPaidAt(LocalDateTime.now());
            payment.setStatus(PaymentStatus.SUCCESS.name());
        } else {
            payment.setStatus(PaymentStatus.FAILED.name());
        }

        payment = paymentRepository.save(payment);

        paymentMapper.toResponse(payment);
    }
}