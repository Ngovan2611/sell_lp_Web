package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.service.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.security.Principal;
import java.util.List;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class HomeController {

    ProductService productService;

    @GetMapping("/home")
    public String home(Model model,
                       Principal principal) {

        try {
            List<ProductResponse> laps =
                    productService.getAllProductsByCategoryDemo(1)
                            .stream()
                            .limit(5)
                            .toList();

            List<ProductResponse> desks =
                    productService.getAllProductsByCategoryDemo(2)
                            .stream()
                            .limit(5)
                            .toList();

            model.addAttribute("laps", laps);
            model.addAttribute("desks", desks);
            String username = principal.getName();


            model.addAttribute("username", username);
            return "index";

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "index";
    }
}
