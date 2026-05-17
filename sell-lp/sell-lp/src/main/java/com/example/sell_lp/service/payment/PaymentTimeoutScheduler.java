package com.example.sell_lp.service.payment;

import com.example.sell_lp.entity.Payment;
import com.example.sell_lp.enums.OrderStatus;
import com.example.sell_lp.enums.PaymentStatus;
import com.example.sell_lp.repository.payment.PaymentRepository;
import com.example.sell_lp.service.order.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentTimeoutScheduler {

    PaymentRepository paymentRepository;
    OrderService orderService;

    // Quét định kỳ mỗi 5 phút một lần (300,000 milliseconds)
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cancelExpiredPayments() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -2);
        Date twoHoursAgo = calendar.getTime();

        // Tìm danh sách các giao dịch quá hạn
        List<Payment> expiredPayments = paymentRepository.findExpiredVNPayPayments(twoHoursAgo);

        if (!expiredPayments.isEmpty()) {
            log.info("Phát hiện {} đơn hàng quá 2 giờ chưa thanh toán. Tiến hành hủy...", expiredPayments.size());

            for (Payment payment : expiredPayments) {
                // 1. Cập nhật trạng thái thanh toán sang FAILED (Thất bại)
                payment.setStatus(PaymentStatus.FAILED.name());
                paymentRepository.save(payment);

                // 2. Cập nhật trạng thái đơn hàng tổng sang FAILURE (Đã hủy)
                orderService.updateOrderStatus(payment.getOrder().getOrderId(), OrderStatus.FAILURE.name());

                log.info("Đã tự động hủy đơn hàng ID: #{} do hết hạn 2 giờ thanh toán.", payment.getOrder().getOrderId());
            }
        } else {
            log.info("Không có đơn hàng nào bị quá hạn thanh toán.");
        }
    }
}