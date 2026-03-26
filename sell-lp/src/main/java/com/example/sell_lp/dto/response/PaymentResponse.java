package com.example.sell_lp.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    Integer paymentId;
    String method;
    BigDecimal amount;
    String status;
    String transactionId;
    String responseCode;
    LocalDateTime paidAt;
    Integer orderId;
}