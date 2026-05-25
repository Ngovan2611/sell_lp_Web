package com.example.sell_lp.controller.admin;

import com.example.sell_lp.dto.request.notification.NotificationCreateRequest;
import com.example.sell_lp.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminSendNotificationController {

    NotificationService notificationService;


    @PostMapping("/send/{userId}")
    @ResponseBody
    public ResponseEntity<?> sendNotification(
            @PathVariable String userId,
            @RequestBody NotificationCreateRequest request){

        notificationService.sendToUser(userId, request);

        return ResponseEntity.ok().build();
    }
}
