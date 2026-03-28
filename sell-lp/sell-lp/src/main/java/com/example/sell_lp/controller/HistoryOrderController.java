package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.order.OrderService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class HistoryOrderController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private OrderService orderService;


    @GetMapping("/history-order")
    public String historyOrder(@CookieValue(value = "jwt", required = false) String token,
                               Model model)
            throws ParseException, JOSEException {
        String username = authenticationService.extractUsernameFromToken(token);

        if(username == null) {
            return "redirect:/login";
        }

        List<OrderResponse> orders = orderService.getOrdersByUsername(username);

        List<OrderResponse> reversedOrders = new ArrayList<>(orders);
        Collections.reverse(reversedOrders);
        model.addAttribute("orders", reversedOrders);
        model.addAttribute("username", username);
        return "history";
    }
    @GetMapping("/history/{orderId}")
    public String getOrderDetail(@PathVariable Integer orderId,
                                 Model model, @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {


        String username = authenticationService.extractUsernameFromToken(token);

        if(username == null) {
            return "redirect:/login";
        }

        OrderResponse order = orderService.getOrderDetail(orderId);

        model.addAttribute("order", order);
        model.addAttribute("username", username);

        return "history-detail";
    }
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {

        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok("Hủy đơn thành công");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
