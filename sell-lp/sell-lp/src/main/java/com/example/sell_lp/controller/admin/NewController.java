package com.example.sell_lp.controller.admin;

import com.example.sell_lp.dto.request.news.NewRequest;
import com.example.sell_lp.dto.response.news.NewResponse;
import com.example.sell_lp.service.news.NewService;
import com.example.sell_lp.service.variant.ImageUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewController {

    NewService newService;
    ImageUploadService imageUploadService;

    // 1. TRANG HIỂN THỊ DANH SÁCH TIN TỨC
    @GetMapping
    public String listNews(Model model) {
        List<NewResponse> newsList = newService.getAllNews();
        model.addAttribute("newsList", newsList);
        model.addAttribute("newsRequest", new NewRequest()); // Đổ object rỗng phục vụ form th:object
        model.addAttribute("pageTitle", "Quản Lý Tin Tức - Bài Viết");
        return "admin/new-manage";
    }

    // 2. REST API: XỬ LÝ THÊM MỚI BÀI VIẾT
    @PostMapping("/save")
    public ResponseEntity<?> saveNews(@ModelAttribute("newsRequest") NewRequest newRequest) {
        try {
            newService.saveNews(newRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đăng bài viết tin tức thành công!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: Không thể lưu bài viết! Chi tiết: " + e.getMessage()
            ));
        }
    }

    // 3. REST API: XỬ LÝ CẬP NHẬT BÀI VIẾT
    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateNews(@PathVariable Integer id, @ModelAttribute("newsRequest") NewRequest newRequest) {
        try {
            newService.updateNews(id, newRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật bài viết thành công!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: Không thể cập nhật bài viết!"
            ));
        }
    }

    // 4. REST API: BẬT/TẮT TRẠNG THÁI CÔNG KHAI (ẨN/HIỆN)
    @PostMapping("/toggle/{id}")
    public ResponseEntity<?> toggleNewsStatus(@PathVariable Integer id) {
        try {
            newService.toggleActive(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thay đổi trạng thái hiển thị bài viết thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Thao tác thay đổi trạng thái thất bại!"
            ));
        }
    }

    // 5. REST API: XÓA BÀI VIẾT KHỎI HỆ THỐNG
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Integer id) {
        try {
            newService.deleteNews(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa bài viết thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: Không thể xóa bài viết này!"
            ));
        }
    }

    // 6. API AJAX UPLOAD ẢNH BÌA TIN TỨC
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        try {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().build();
            }
            for (MultipartFile file : files) {
                String url = imageUploadService.uploadImage(file, "news");
                urls.add(url);
            }
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}