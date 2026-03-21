package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CategoryService;
import com.example.sell_lp.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class HomeController {
    CategoryService categoryService;

    AuthenticationService authenticationService;

    ProductService productService;



    @GetMapping("/home")
    public String home(Model model,
                       @CookieValue(value = "jwt", required = false) String token) {

        try {
            List<CategoryResponse> categories = categoryService.findAll();
            model.addAttribute("categories", categories);

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
            String username = authenticationService.extractUsernameFromToken(token);


            model.addAttribute("username", username);
            return "index";

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "index";
    }
}
