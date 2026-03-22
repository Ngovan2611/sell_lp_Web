package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CategoryService;
import com.example.sell_lp.service.ProductService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    AuthenticationService authenticationService;

    CategoryService categoryService;

    @GetMapping("/products")
    public String showProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            @CookieValue(value = "jwt", required = false) String token
    ) throws ParseException, JOSEException {

        if (token == null) {
            return "redirect:/login";
        }

        String username = authenticationService.extractUsernameFromToken(token);
        List<CategoryResponse> categories = categoryService.findAll();
        if (username == null) {
            return "redirect:/login";
        }

        int pageSize = 10;
        PageRequest pageable = PageRequest.of(page, pageSize);
        Page<ProductResponse> productPage;
        if(categoryId == null) {
            productPage =
                    productService.getAllProducts(pageable);
        }else {
            productPage =
                    productService.getAllProductsByCategory(categoryId, pageable);
        }
        model.addAttribute("username", username);
        model.addAttribute("categories", categories);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categoryId", categoryId);

        return "products";
    }
}
