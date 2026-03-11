package com.example.sell_lp.controller;

import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CategoryService;
import com.example.sell_lp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.ParseException;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/home")
    public String home(Model model,
                       @CookieValue(value = "jwt", required = false) String token) {


        try {
            model.addAttribute("categories", categoryService.findAll());

            String username = authenticationService.extractUsernameFromToken(token);


            model.addAttribute("username", username);

            return "index";

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "index";

    }
}
