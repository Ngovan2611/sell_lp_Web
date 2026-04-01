package com.example.sell_lp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer paymentId;
    String method;
    BigDecimal amount;
    String status;
    String transactionId;
    String responseCode;
    LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;
}
