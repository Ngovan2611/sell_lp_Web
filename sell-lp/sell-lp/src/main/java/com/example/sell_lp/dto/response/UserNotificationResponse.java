package com.example.sell_lp.dto.response;

import com.example.sell_lp.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNotificationResponse {
    private Long notificationId;
    private String title;
    private String message;
    private NotificationType type;

    private boolean read;
    private LocalDateTime createdAt;
}