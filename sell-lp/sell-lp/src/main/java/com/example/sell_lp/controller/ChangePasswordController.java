package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.UserChangePasswordRequest;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.service.user.AuthService;
import com.example.sell_lp.service.user.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;


@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class ChangePasswordController {
    UserService userService;
    AuthService authService;

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model, Principal principal) {
        if(principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        UserResponse user = userService.getUserByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("provider", user.getProvider());
        return "password-change";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute UserChangePasswordRequest request,
                                 Model model, Principal principal) {
        if(principal == null) {
            return  "redirect:/login";
        }
        String username = principal.getName();
        UserResponse user = userService.getUserByUsername(username);

        model.addAttribute("username", username);
        model.addAttribute("provider", user.getProvider());


        try {

            authService.updatePassword(request, username);

            model.addAttribute("success", "Đổi mật khẩu thành công!");
            return "password-change";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "password-change";
        }
    }
}
