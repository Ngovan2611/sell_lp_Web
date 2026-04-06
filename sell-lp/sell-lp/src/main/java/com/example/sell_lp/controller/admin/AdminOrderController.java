package com.example.sell_lp.controller.admin;

import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.service.order.AdminOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminOrderController {

    AdminOrderService orderService;

    @GetMapping
    public String getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<OrderResponse> orderPage = orderService.getAllOrders(pageable);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());

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