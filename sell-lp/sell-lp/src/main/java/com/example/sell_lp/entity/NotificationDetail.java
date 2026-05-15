package com.example.sell_lp.entity;


import com.example.sell_lp.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_user")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NotificationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long notificationId;
    String userId;

    // SNAPSHOT DATA
    String title;
    String message;
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    boolean isRead;
    LocalDateTime readAt;
    LocalDateTime createdAt;
}