package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.UserUpdateRequest;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.UserService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/profile")
public class ProfileController {

    UserService userService;

    AuthenticationService authenticationService;

    @GetMapping
    public String showProfile(Model model,
                              @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {
        if(token == null){
            return "redirect:/login";
        }
        String username =  authenticationService.extractUsernameFromToken(token);
        UserResponse user = userService.getUserByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("user", user);
        return "profile";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute UserUpdateRequest request,
            RedirectAttributes redirectAttributes,
            java.security.Principal principal) {

        String username = principal.getName();

        UserResponse user = userService.getUserByUsername(username);
        userService.updateUser(user.getUserId(), request);
        redirectAttributes.addFlashAttribute("success", "cập nhật thành công!");

        return "redirect:/profile";
    }
}
