package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.service.AuthenticationService;
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

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {


    ProductService productService;
    AuthenticationService authenticationService;

    @GetMapping("/products")
    public String showProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            Model model,
            @CookieValue(value = "jwt", required = false) String token
    ) throws ParseException, JOSEException {

        if (token == null) {
            return "redirect:/login";
        }
        String username = authenticationService.extractUsernameFromToken(token);
        if (username == null) {
            return "redirect:/login";
        }

        PageRequest pageable = PageRequest.of(page, 10);
        Page<ProductResponse> productPage = productService.getProductsFiltered(categoryId, keyword, sort, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("username", username);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "products";
    }
}