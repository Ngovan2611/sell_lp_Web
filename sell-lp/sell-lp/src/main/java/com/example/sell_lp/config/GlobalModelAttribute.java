package com.example.sell_lp.config;

import com.example.sell_lp.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ControllerAdvice
public class GlobalModelAttribute {

    CategoryService categoryService;

    @ModelAttribute
    public void addGlobalAttributes(HttpServletRequest request, Model model) {

        if (!request.getRequestURI().startsWith("/admin")) {
            model.addAttribute("categories", categoryService.findAll());
        }
    }
}