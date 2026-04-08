package com.example.sell_lp.controller.admin;

import com.example.sell_lp.service.user.UserService;
import com.example.sell_lp.service.order.TotalOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminPageController {

    UserService userService;

    TotalOrderService totalOrderService;

    @GetMapping({"", "/", "/index"})
    public String adminIndex(Model model) {
        List<Double> revenueData = totalOrderService.getRevenueLast7Days();
        List<String> dayLabels = totalOrderService.getDaysLast7Days();

        long successCount = totalOrderService.countByStatusSuccess();
        long pendingCount = totalOrderService.countByStatusPendingAndPreparing();
        long failureCount = totalOrderService.countByStatusFailed();

        model.addAttribute("revenueData", revenueData);
        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("statusData", List.of(successCount, pendingCount, failureCount));

        model.addAttribute("totalRevenue", totalOrderService.getTotalOrder());
        model.addAttribute("totalCustomers", userService.countUser());
        model.addAttribute("newOrders", totalOrderService.countByStatusPending());

        return "admin/index";
    }
}