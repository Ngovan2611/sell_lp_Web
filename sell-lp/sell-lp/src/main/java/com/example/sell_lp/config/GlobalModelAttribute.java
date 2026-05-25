package com.example.sell_lp.config;

import com.example.sell_lp.dto.response.notification.UserNotificationResponse;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.service.category.CategoryService;
import com.example.sell_lp.service.notification.NotificationService;
import com.example.sell_lp.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ControllerAdvice
@Order(Integer.MIN_VALUE)
public class GlobalModelAttribute {

    NotificationService notificationService;
    CategoryService categoryService;
    UserService userService;
    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {

        if (!request.getRequestURI().startsWith("/admin")) {
            model.addAttribute("categories", categoryService.getDisplayedCategoriesForUser());
        }
    }


    @ModelAttribute("notifications")
    public List<UserNotificationResponse> getGlobalNotifications() {
        // 1. Lấy đối tượng Authentication từ SecurityContext
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        // 2. Kiểm tra an toàn: Nếu chưa đăng nhập hoặc là AnonymousUser thì trả về list trống ngay
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return Collections.emptyList();
        }

        try {
            // 3. Lấy username an toàn từ auth.getName() (Hoạt động cho cả UserDetails và OAuth2)
            String username = auth.getName();

            User userExist = userService.getUserEntityByUsername(username);
            if (userExist != null) {
                List<UserNotificationResponse> list = notificationService.getUserNotifications(userExist.getUserId());
                return (list != null) ? list : Collections.emptyList();
            }
        } catch (Exception e) {
            // Ghi log chi tiết để debug thay vì chỉ in message
            System.err.println("DEBUG: Lỗi khi lấy thông báo cho user: " + e.getMessage());
        }

        return Collections.emptyList();
    }
    @ModelAttribute("unreadCount")
    public long getUnreadCount() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return 0;
        }

        try {

            String username = auth.getName();

            User userExist = userService.getUserEntityByUsername(username);

            if (userExist != null) {
                return notificationService.countUnread(userExist.getUserId());
            }

        } catch (Exception e) {
            System.err.println("DEBUG unread count error: " + e.getMessage());
        }

        return 0;
    }
}