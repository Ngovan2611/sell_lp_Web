package com.example.sell_lp.repository.notification;

import com.example.sell_lp.entity.Notifications;
import com.example.sell_lp.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notifications, Long> {

    Optional<Notifications> findByType(NotificationType type);

    List<Notifications> findAllByType(NotificationType type);
}
