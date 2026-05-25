package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.request.notification.NotificationCreateRequest;
import com.example.sell_lp.dto.response.notification.NotificationResponse;
import com.example.sell_lp.dto.response.notification.NotificationUserResponse;
import com.example.sell_lp.dto.response.notification.UserNotificationResponse;
import com.example.sell_lp.entity.NotificationDetail;
import com.example.sell_lp.entity.Notifications;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // =========================
    // 1. NOTIFICATION TEMPLATE
    // =========================

    NotificationResponse toResponse(Notifications entity);

    List<NotificationResponse> toResponseList(List<Notifications> entities);

    @Mapping(target = "id", ignore = true)
    Notifications toEntity(NotificationCreateRequest request);

    // =========================
    // 2. NOTIFICATION SNAPSHOT (USER)
    // =========================

    NotificationUserResponse toUserResponse(NotificationDetail entity);

    List<NotificationUserResponse> toUserResponseList(List<NotificationDetail> entities);

    // =========================
    // 3. UI RESPONSE (INBOX)
    // =========================

    // =========================
    // 3. UI RESPONSE (INBOX)
    // =========================

    @Mapping(source = "id", target = "notificationId")

    UserNotificationResponse toInboxResponse(NotificationDetail entity);

    List<UserNotificationResponse> toInboxResponseList(List<NotificationDetail> entities);

    // =========================
    // 4. SNAPSHOT CREATION
    // =========================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    NotificationDetail toSnapshotEntity(Notifications notification,
                                        @Context Long userId);
}