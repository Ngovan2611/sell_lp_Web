package com.example.sell_lp.dto.response;

import com.example.sell_lp.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationUserResponse {
    private Long id;

    private Long notificationId;
    private Long userId;

    // snapshot data
    private String title;
    private String message;
    private NotificationType type;

    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}