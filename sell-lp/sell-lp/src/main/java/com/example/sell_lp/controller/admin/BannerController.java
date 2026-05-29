package com.example.sell_lp.controller.admin;

import com.example.sell_lp.dto.request.banner.BannerRequest;
import com.example.sell_lp.dto.response.banner.BannerResponse;
import com.example.sell_lp.service.banner.BannerService;
import com.example.sell_lp.service.variant.ImageUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/banners")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
public class BannerController {

    BannerService bannerService;
    ImageUploadService imageUploadService;
    // 1. HIỂN THỊ DANH SÁCH BANNER
    @GetMapping
    public String listBanners(Model model) {
        List<BannerResponse> banners = bannerService.getAllBanners();
        model.addAttribute("banners", banners);
        model.addAttribute("bannerRequest", new BannerRequest()); // Phục vụ cho Modal thêm mới
        model.addAttribute("pageTitle", "Quản Lý Banner Giao Diện");
        return "admin/banner-manage"; // Trả về file HTML trong thư mục templates/admin/
    }

    // 2. XỬ LÝ THÊM MỚI BANNER

// ... Các code cũ giữ nguyên ...

    // 2. XỬ LÝ THÊM MỚI BANNER
    @PostMapping("/save")
    public ResponseEntity<?> saveBanner(@ModelAttribute("bannerRequest") BannerRequest bannerRequest) {
        try {
            bannerService.saveBanner(bannerRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thêm mới banner thành công!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Thêm mới thất bại: " + e.getMessage()
            ));
        }
    }

    // 3. LẤY CHI TIẾT 1 BANNER (Dùng cho Modal Sửa)
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseEntity<?> getBannerDetail(@PathVariable Integer id) {
        try {
            BannerResponse bannerResponse = bannerService.getBannerById(id);
            return ResponseEntity.ok(bannerResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy thông tin banner!"
            ));
        }
    }

    // 4. XỬ LÝ CẬP NHẬT BANNER (UPDATE)
    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateBanner(@PathVariable Integer id,
                                          @ModelAttribute("bannerRequest") BannerRequest bannerRequest) {
        try {
            bannerService.updateBanner(id, bannerRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật thông tin banner thành công!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Cập nhật thất bại: " + e.getMessage()
            ));
        }
    }

    // 5. BẬT/TẮT NHANH TRẠNG THÁI ẨN HIỆN
    @PostMapping("/toggle/{id}") // Chuyển từ GET sang POST để chuẩn REST API bảo mật hơn
    public ResponseEntity<?> toggleBannerStatus(@PathVariable Integer id) {
        try {
            bannerService.toggleActive(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Thay đổi trạng thái hiển thị thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Thao tác thất bại!"
            ));
        }
    }

    // 6. XÓA BANNER KHỎI HỆ THỐNG
    @DeleteMapping("/delete/{id}") // Chuyển từ GET sang DELETE đúng chuẩn
    public ResponseEntity<?> deleteBanner(@PathVariable Integer id) {
        try {
            bannerService.deleteBanner(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa banner khỏi hệ thống thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Xóa thất bại: " + e.getMessage()
            ));
        }
    }
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        try {
            // Kiểm tra xem file gửi lên có bị trống không
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().build();
            }

            for (MultipartFile file : files) {
                // Thay "products" hoặc "banners" tùy thuộc cấu trúc thư mục của bạn
                String url = imageUploadService.uploadImage(file, "banners");
                urls.add(url);
            }
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            // In toàn bộ vết lỗi (Stack Trace) ra màn hình đen (Console) của Spring Boot để xem lỗi gì
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}