package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.banner.BannerResponse;
import com.example.sell_lp.dto.response.news.NewResponse;
import com.example.sell_lp.dto.response.product.ProductResponse;
import com.example.sell_lp.service.banner.BannerService;
import com.example.sell_lp.service.news.NewService;
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
    BannerService bannerService;
    NewService newService; // Thêm tiêm NewService vào đây

    @GetMapping("/home")
    public String home(Model model, Principal principal) {

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

            List<BannerResponse> activeBanners = bannerService.getActiveBannersForUser()
                    .stream()
                    .toList();

            // 1. Lấy danh sách tin tức, lọc chỉ lấy bài viết hiển thị (active = true)
            List<NewResponse> activeNews = newService.getAllNews()
                    .stream()
                    .filter(NewResponse::isActive) // Chỉ lấy các bài viết công khai
                    .toList();

            model.addAttribute("laps", laps);
            model.addAttribute("desks", desks);
            model.addAttribute("banners", activeBanners);
            model.addAttribute("newsList", activeNews); // 2. Đẩy danh sách bài viết sang Thymeleaf

            if (principal != null) {
                String username = principal.getName();
                model.addAttribute("username", username);
            }

            return "index";

        } catch (Exception e) {
            System.out.println("Lỗi Home: " + e.getMessage());
            e.printStackTrace();
        }
        return "index";
    }
}