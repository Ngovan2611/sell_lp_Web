package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.service.order.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@PreAuthorize("isAuthenticated()")
public class HistoryOrderController {

    OrderService orderService;


    @GetMapping("/history-order")
    public String historyOrder(Principal principal,
                               Model model) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();



        List<OrderResponse> orders = orderService.getOrdersByUsername(username);

        List<OrderResponse> reversedOrders = new ArrayList<>(orders);
        Collections.reverse(reversedOrders);
        model.addAttribute("orders", reversedOrders);
        model.addAttribute("username", username);
        return "history";
    }
    @GetMapping("/history/{orderId}")
    public String getOrderDetail(@PathVariable Integer orderId,
                                 Model model, Principal principal) {


        String username = principal.getName();
        if(username == null) {
            return "redirect:/login";
        }

        OrderResponse order = orderService.getOrderDetail(orderId, username);

        model.addAttribute("order", order);
        model.addAttribute("username", username);

        return "history-detail";
    }
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id, Principal principal) {
        String username = principal.getName();
        try {
            orderService.cancelOrderByUser(id , username);
            return ResponseEntity.ok("Hủy đơn thành công");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
