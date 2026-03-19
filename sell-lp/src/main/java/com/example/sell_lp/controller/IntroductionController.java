package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IntroductionController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    CategoryService categoryService;


    @GetMapping("/introduction")
    public String intro(Model model,
                        @CookieValue (value = "jwt", required = false) String token) {
        try {
            String username = authenticationService.extractUsernameFromToken(token);
            List<CategoryResponse> categories = categoryService.findAll();
            model.addAttribute("categories", categories);
            model.addAttribute("username", username);

        }catch (Exception e){
            return  "redirect:/login";
        }
        
        return "/introduction";
    }
}
