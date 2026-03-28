package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.AuthenticationRequest;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class LoginController {

    UserService userService;
    AuthenticationService authenticationService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute AuthenticationRequest request,
                            RedirectAttributes redirectAttributes,
                            HttpServletResponse response,
                            Model model
                            ) {

        try {
            User user = userService.login(request);


            if (user != null) {
                var token = authenticationService.authenticate(request);

                Cookie cookie = new Cookie("jwt", token.getToken());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(24 * 60 * 60);
                response.addCookie(cookie);
                redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");
                return "redirect:/home";
            }


            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "login";
        }catch (Exception e) {
            model.addAttribute("error", "Tài khoản không hợp lệ hoặc đã bị khóa!");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
