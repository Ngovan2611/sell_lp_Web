package com.example.sell_lp.service.payment;

import com.example.sell_lp.component.TransactionCodeGenerator;
import com.example.sell_lp.dto.request.order.OrderCreationRequest;
import com.example.sell_lp.dto.request.payment.PaymentRequest;
import com.example.sell_lp.entity.Order;
import com.example.sell_lp.entity.Payment;
import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.enums.PaymentMethod;
import com.example.sell_lp.enums.PaymentStatus;
import com.example.sell_lp.mapper.PaymentMapper;
import com.example.sell_lp.repository.order.OrderRepository;
import com.example.sell_lp.repository.payment.PaymentRepository;
import com.example.sell_lp.service.order.OrderService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class PaymentService {

    PaymentRepository paymentRepository;
    OrderRepository orderRepository;
    PaymentMapper paymentMapper;
    TransactionCodeGenerator transactionCodeGenerator;
    OrderService orderService;

    public Payment createOrderAndInitPayment(OrderCreationRequest request, String paymentMethod) {
        // 1. Tạo đơn hàng và đặt trạng thái ban đầu là PENDING
        Order savedOrder = orderService.save(request);
        orderService.updateOrderStatus(savedOrder.getOrderId(), OrderStatus.PENDING.name());

        // 2. Map dữ liệu sang Entity
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(savedOrder.getOrderId());
        paymentRequest.setMethod(PaymentMethod.valueOf(paymentMethod.toUpperCase()));
        paymentRequest.setAmount(savedOrder.getTotalAmount());
        paymentRequest.setStatus(PaymentStatus.PENDING.name());

        Payment payment = paymentMapper.toEntity(paymentRequest);
        payment.setOrder(savedOrder);

        // 3. Xử lý riêng cho VN_PAY: Phải sinh mã trước để lưu vào database làm vnp_TxnRef đối soát
        if (PaymentMethod.VN_PAY.name().equalsIgnoreCase(paymentMethod)) {
            String uniqueTxnCode = transactionCodeGenerator.generate();
            payment.setTransactionId(uniqueTxnCode); // Ghi nhận mã LAP... vào cột transaction_id trong DB
        }

        return paymentRepository.save(payment);
    }

    public PaymentRequest processVNPayReturn(Map<String, String> params) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String amountStr = params.get("vnp_Amount");
        String vnp_TxnRef = params.get("vnp_TxnRef"); // Mã LAP... mà VNPay gửi ngược lại

        // Đối soát ngược từ DB: Tìm bản ghi có transaction_id trùng với mã vnp_TxnRef
        Payment payment = paymentRepository.findByTransactionId(vnp_TxnRef);
        if (payment == null) {
            throw new IllegalStateException("Không tìm thấy thông tin giao dịch cho mã: " + vnp_TxnRef);
        }

        BigDecimal realAmount = new BigDecimal(amountStr).divide(new BigDecimal(100));
        payment.setAmount(realAmount);
        payment.setResponseCode(vnp_ResponseCode); // Lưu mã phản hồi VNPay (Ví dụ: 00, 24...) vào DB

        PaymentRequest resultDto = new PaymentRequest();
        resultDto.setOrderId(payment.getOrder().getOrderId());
        resultDto.setAmount(realAmount);
        resultDto.setResponseCode(vnp_ResponseCode);

        if ("00".equals(vnp_ResponseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS.name());
            payment.setPaidAt(LocalDateTime.now());
            orderService.updateOrderStatus(payment.getOrder().getOrderId(), OrderStatus.PENDING.name());
        } else {
            payment.setStatus(PaymentStatus.FAILED.name());
            orderService.updateOrderStatus(payment.getOrder().getOrderId(), OrderStatus.FAILURE.name());
        }

        paymentRepository.save(payment);
        return resultDto;
    }

    public void completeCODPayment(Integer orderId) {
        Payment payment = paymentRepository.findByOrderOrderId(orderId);
        if (payment != null) {
            payment.setStatus(PaymentStatus.SUCCESS.name());
            payment.setPaidAt(LocalDateTime.now());
            if (payment.getTransactionId() == null) {
                payment.setTransactionId(transactionCodeGenerator.generate());
            }
            paymentRepository.save(payment);
        }
    }

    public Payment getByOrderOrderId(Integer orderId) {
        return paymentRepository.findByOrderOrderId(orderId);
    }
}