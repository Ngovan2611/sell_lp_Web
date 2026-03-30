package com.example.sell_lp.controller;


import com.example.sell_lp.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IntroductionController {
    AuthenticationService authenticationService;

    @GetMapping("/introduction")
    public String intro(Model model,
                        @CookieValue (value = "jwt", required = false) String token) {
        try {
            String username = authenticationService.extractUsernameFromToken(token);
            model.addAttribute("username", username);

        }catch (Exception e){
            return  "redirect:/login";
        }
        
        return "/introduction";
    }
}
