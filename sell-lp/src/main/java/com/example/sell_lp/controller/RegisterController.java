package com.example.sell_lp.controller;

import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {
    @Autowired
    private UserService userService;
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    @PostMapping("register")
    public String registerSubmit(@ModelAttribute @Valid UserCreationRequest userCreationRequest,
                                 BindingResult result,
                                 Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            User user = userService.createUser(userCreationRequest);
            redirectAttributes.addFlashAttribute("success", "Đăng kí thành công");
            return "redirect:/login";
        }catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }
}


