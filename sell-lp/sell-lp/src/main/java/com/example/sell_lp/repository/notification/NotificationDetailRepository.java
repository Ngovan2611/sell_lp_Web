package com.example.sell_lp.repository.notification;


import com.example.sell_lp.entity.NotificationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationDetailRepository extends JpaRepository<NotificationDetail, Long> {

    List<NotificationDetail>
    findByUserIdOrderByIsReadAscCreatedAtDesc(String userId);

    List<NotificationDetail> findByUserIdAndIsReadFalse(String userId);

    Optional<NotificationDetail> findByUserIdAndNotificationId(String userId, Long notificationId);

    long countByUserIdAndIsReadFalse(String userId);}
