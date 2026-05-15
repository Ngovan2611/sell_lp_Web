package com.example.sell_lp.repository.payment;


import com.example.sell_lp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderOrderId(Integer paymentId);
    Payment findByTransactionId(String transactionId);
    // HÀM QUÉT: Tìm các payment VNPay chưa thanh toán và đã quá hạn 2 tiếng
    // Giả sử thực tế thực thể Payment hoặc Order có trường lưu thời gian tạo, ở đây sử dụng order.orderDate làm mốc
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' " +
            "AND p.method = 'VN_PAY' " +
            "AND p.order.orderDate < :targetTime")
    List<Payment> findExpiredVNPayPayments(@Param("targetTime") java.util.Date targetTime);

}
