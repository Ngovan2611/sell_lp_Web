package com.example.sell_lp.config;

import com.example.sell_lp.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;



@ControllerAdvice
public class GlobalModelAttribute {

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {

        if (!request.getRequestURI().startsWith("/admin")) {
            model.addAttribute("categories", categoryService.findAll());
        }
    }
}