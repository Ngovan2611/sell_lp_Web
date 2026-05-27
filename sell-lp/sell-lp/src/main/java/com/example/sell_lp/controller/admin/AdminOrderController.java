package com.example.sell_lp.controller.admin;

import com.example.sell_lp.dto.response.order.OrderResponse;
import com.example.sell_lp.service.order.AdminOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    AdminOrderService orderService;

    @GetMapping
    public String getAllOrders(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        // Sắp xếp đơn hàng theo ngày tạo mới nhất (createdAt)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Gọi đúng hàm tìm kiếm nâng cao từ Service
        Page<OrderResponse> orderPage = orderService.searchOrdersAdmin(orderId, customerName, phone, status, pageable);

        // Đẩy dữ liệu danh sách và phân trang ra Thymeleaf
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());

        // Gửi ngược lại các giá trị tìm kiếm để giữ trạng thái trên các ô Input/Select của HTML
        model.addAttribute("orderId", orderId);
        model.addAttribute("customerName", customerName);
        model.addAttribute("phone", phone);
        model.addAttribute("selectedStatus", status);

        return "admin/order-manage";
    }

    @GetMapping("/detail/{id}")
    public String getOrderDetail(@PathVariable Integer id, Model model) {
        OrderResponse order = orderService.getOrderDetailForAdmin(id);
        model.addAttribute("order", order);
        return "admin/order-details";
    }

    @PostMapping("/update-status/{id}")
    @ResponseBody
    public void updateStatus(@PathVariable Integer id, @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
    }

    @PostMapping("/cancel/{id}")
    @ResponseBody
    public void cancelOrder(@PathVariable Integer id) {
        orderService.cancelOrderByAdmin(id);
    }
}