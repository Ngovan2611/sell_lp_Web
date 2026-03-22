package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.OrderCreationRequest;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private OrderService orderService;

    @PostMapping("/order/create")
    public String createOrder(@ModelAttribute OrderCreationRequest request,
                              @CookieValue(value = "jwt", required = false) String token)
            throws Exception {

        String username = authenticationService.extractUsernameFromToken(token);
        if (username == null) {
            return "redirect:/login";
        }

        orderService.save(request);

        return "redirect:/cart";
    }
    @GetMapping("/history")
    public String history() {
        return "history";
    }
}
