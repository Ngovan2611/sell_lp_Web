package com.example.sell_lp.controller;

import com.example.sell_lp.entity.User;
import com.example.sell_lp.service.notification.NotificationService;
import com.example.sell_lp.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationRestController {

    NotificationService notificationService;
    UserService userService;

    @PostMapping("/mark-as-read/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markAsRead(@PathVariable Long id,
                                        Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Phiên làm việc hết hạn");
        }

        try {

            String username = principal.getName();

            User userExist = userService.getUserEntityByUsername(username);

            if (userExist == null) {
                return ResponseEntity.status(404).body("Người dùng không tồn tại");
            }

            notificationService.markAsRead(userExist.getUserId(), id);

            return ResponseEntity.ok("Đã đọc");

        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}