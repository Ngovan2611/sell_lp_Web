package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.UserChangePasswordRequest;
import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CategoryService;
import com.example.sell_lp.service.UserService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import java.text.ParseException;
import java.util.List;

@Controller
public class ChangePasswordController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model,
                                         @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {

        String username = authenticationService.extractUsernameFromToken(token);
        List<CategoryResponse> categories = categoryService.findAll();
        UserResponse user = userService.getUserByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("provider", user.getProvider());
        model.addAttribute("categories", categories);
        return "password-change";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute UserChangePasswordRequest request,
                                 @CookieValue(value = "jwt", required = false) String token,
                                 Model model)
            throws ParseException, JOSEException {
        if(token == null) {
            return  "redirect:/login";
        }



        String username = authenticationService.extractUsernameFromToken(token);


        model.addAttribute("username", username);


        try {

            userService.updatePassword(request, username);

            model.addAttribute("success", "Đổi mật khẩu thành công!");
            return "password-change";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "password-change";
        }
    }
}
