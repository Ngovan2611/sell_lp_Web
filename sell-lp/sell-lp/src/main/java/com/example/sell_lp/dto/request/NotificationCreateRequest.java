package com.example.sell_lp.dto.request;


import com.example.sell_lp.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationCreateRequest {
    private String title;
    private String message;
    private NotificationType type;
}
