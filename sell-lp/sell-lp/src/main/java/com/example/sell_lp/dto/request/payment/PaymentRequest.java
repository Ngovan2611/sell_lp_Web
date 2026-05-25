package com.example.sell_lp.dto.request.payment;

import com.example.sell_lp.enums.PaymentMethod;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    Integer orderId;
    PaymentMethod method;
    BigDecimal amount;
    String transactionId;
    String responseCode;
    String status;
}