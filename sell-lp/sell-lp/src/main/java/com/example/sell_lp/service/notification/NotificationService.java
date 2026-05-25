package com.example.sell_lp.service.notification;

import com.example.sell_lp.dto.request.notification.NotificationCreateRequest;
import com.example.sell_lp.dto.response.notification.UserNotificationResponse;
import com.example.sell_lp.entity.NotificationDetail;
import com.example.sell_lp.entity.Notifications;
import com.example.sell_lp.enums.NotificationType;
import com.example.sell_lp.mapper.NotificationMapper;
import com.example.sell_lp.repository.notification.NotificationDetailRepository;
import com.example.sell_lp.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDetailRepository detailRepository;
    private final NotificationMapper notificationMapper;
    public Notifications getByType(NotificationType type) {
        return notificationRepository.findByType(type)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + type));
    }

    // =========================
    // SEND TO USER (snapshot)
    // =========================
    public void sendToUser(NotificationType type, String message, String userId) {

        Notifications notification = getByType(type);

        NotificationDetail detail = new NotificationDetail();
        detail.setNotificationId(notification.getId());
        detail.setUserId(userId);

        detail.setTitle(notification.getTitle());
        detail.setMessage(notification.getMessage() + " " + message);
        detail.setType(notification.getType());

        detail.setRead(false);
        detail.setCreatedAt(LocalDateTime.now());

        detailRepository.save(detail);
    }

    public List<UserNotificationResponse> getUserNotifications(String userId) {
        List<NotificationDetail> details = detailRepository.findByUserIdOrderByIsReadAscCreatedAtDesc(userId);

        // Sử dụng mapper để chuyển đổi List Entity -> List DTO
        return notificationMapper.toInboxResponseList(details);
    }

    @Transactional
    public void markAsRead(String userId, Long id) {
        // 1. Tìm thông báo
        NotificationDetail detail = detailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thông báo không tồn tại"));

        // 2. Bảo mật: Kiểm tra xem thông báo này có đúng của user đang yêu cầu không
        if (!detail.getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này");
        }

        // 3. Nếu đã đọc rồi thì không cần update lại tốn tài nguyên
        if (detail.isRead()) return;

        detail.setRead(true);
        detail.setReadAt(LocalDateTime.now());

        detailRepository.save(detail);
    }
    public long countUnread(String userId) {
        return detailRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void sendToUser(String userId,
                           NotificationCreateRequest request){

        Notifications notification =
                getByType(NotificationType.OTHER);

        NotificationDetail detail =
                new NotificationDetail();

        detail.setNotificationId(notification.getId());

        detail.setUserId(userId);

        detail.setTitle(request.getTitle());

        detail.setMessage(request.getMessage());

        detail.setType(NotificationType.OTHER);

        detail.setRead(false);

        detail.setCreatedAt(LocalDateTime.now());

        detailRepository.save(detail);
    }
}