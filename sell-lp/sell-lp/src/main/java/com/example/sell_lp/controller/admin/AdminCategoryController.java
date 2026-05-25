package com.example.sell_lp.controller.admin;



import com.example.sell_lp.dto.request.category.CategoryCreationRequest;
import com.example.sell_lp.dto.request.category.CategoryUpdateRequest;
import com.example.sell_lp.dto.response.category.CategoryResponse;
import com.example.sell_lp.entity.Category;
import com.example.sell_lp.service.category.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryController {

    CategoryService categoryService;

    // Hiển thị trang giao diện gốc ban đầu
    @GetMapping
    public String showCategoryPage(Model model) {
        List<CategoryResponse> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "admin/categories-manage";
    }

    // API Thêm mới bằng Ajax
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> saveCategory(@RequestBody CategoryCreationRequest request) {
        try {
            categoryService.save(request);
            return ResponseEntity.ok("Thêm danh mục mới thành công!");
        } catch (RuntimeException e) {
            // Trả về lỗi 400 kèm tin nhắn bắt được từ Service (ví dụ trùng tên)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Có lỗi hệ thống xảy ra!");
        }
    }

    // API Cập nhật bằng Ajax
    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateCategory(@PathVariable("id") Integer id,
                                            @RequestBody CategoryUpdateRequest request) {
        try {
            categoryService.update(id, request);
            return ResponseEntity.ok("Cập nhật danh mục thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Không thể cập nhật danh mục!");
        }
    }
    // API Xóa danh mục bằng Ajax
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Integer id) {
        try {
            categoryService.delete(id); // Hoặc tên hàm xóa tương ứng trong Service của bạn
            return ResponseEntity.ok("Xóa danh mục thành công!");
        } catch (RuntimeException e) {
            // Trả về lỗi nếu danh mục đang có sản phẩm ràng buộc không thể xóa
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Không thể xóa danh mục này!");
        }
    }
}