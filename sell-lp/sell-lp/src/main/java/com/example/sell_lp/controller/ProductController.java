package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.service.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("isAuthenticated()")
public class ProductController {


    ProductService productService;

    @GetMapping("/products")
    public String showProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            Model model,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();


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