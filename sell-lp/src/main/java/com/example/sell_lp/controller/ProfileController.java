package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.UserUpdateRequest;
import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CategoryService;
import com.example.sell_lp.service.UserService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileController {

    UserService userService;

    AuthenticationService authenticationService;

    @GetMapping("/profile")
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

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute UserUpdateRequest request,
            RedirectAttributes redirectAttributes,
            @CookieValue("jwt") String token
    ) throws Exception {

        String username = authenticationService.extractUsernameFromToken(token);

        UserResponse user = userService.getUserByUsername(username);

        userService.updateUser(user.getUserId(), request);
        redirectAttributes.addFlashAttribute("success", "cập nhật thành công!");

        return "redirect:/profile";
    }
}
