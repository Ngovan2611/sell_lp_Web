package com.example.sell_lp.controller.admin;


import com.example.sell_lp.dto.response.user.UserResponse;
import com.example.sell_lp.entity.Role;
import com.example.sell_lp.service.authorization.RoleService;
import com.example.sell_lp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor

public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;


    @GetMapping()
    public String listUsers(Model model,
                            @RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "keyword", required = false) String keyword) {

        int pageSize = 10; // Cấu hình hiển thị 10 khách hàng trên một trang

        // Phân trang & tự động sắp xếp theo ngày tạo mới nhất (createdAt) giảm dần
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        // Lấy dữ liệu đã lọc theo Role USER và phân trang từ Service
        Page<UserResponse> userPage = userService.getUsers(keyword, pageable);

        // Đẩy dữ liệu ra giao diện Thymeleaf
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("keyword", keyword); // Giữ từ khóa lại trên ô input tìm kiếm sau khi reload

        return "admin/user-manage";
    }

    @PostMapping("/toggle-status/{id}")
    @ResponseBody
    public ResponseEntity<String> toggleStatus(@PathVariable String id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok("Success");
    }
}
