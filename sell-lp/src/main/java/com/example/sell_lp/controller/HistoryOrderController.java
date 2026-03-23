package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.OrderResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.OrderService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.text.ParseException;
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
        model.addAttribute("orders", orders);
        model.addAttribute("username", username);
        return "history";

    }

}
