package com.example.sell_lp.dto.response;


import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    private Integer orderId;

    Date orderDate;

    String recipientName;
    String phone;

    String fullAddress;
    String street;
    String ward;
    String district;
    String city;

    Double lat;
    Double lng;

    String status;

    BigDecimal totalAmount;

    Date createdAt;
}
