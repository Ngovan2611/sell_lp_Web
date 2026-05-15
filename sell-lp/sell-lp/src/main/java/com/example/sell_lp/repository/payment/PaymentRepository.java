package com.example.sell_lp.repository.payment;


import com.example.sell_lp.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderOrderId(Integer paymentId);
}
