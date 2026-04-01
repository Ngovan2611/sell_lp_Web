package com.example.sell_lp.controller.admin;


import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.entity.Role;
import com.example.sell_lp.service.RoleService;
import com.example.sell_lp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor

public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;


    @GetMapping
    public String listUsers(Model model) {
        Role defaultRole = roleService.getByRoleName(com.example.sell_lp.enums.Role.USER.name());
        List<UserResponse> users = userService.getAllUsersByRoles(defaultRole);
        model.addAttribute("users", users);
        return "admin/user-manage";
    }

    @PostMapping("/toggle-status/{id}")
    @ResponseBody
    public ResponseEntity<String> toggleStatus(@PathVariable String id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok("Success");
    }
}
