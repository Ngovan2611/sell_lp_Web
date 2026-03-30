package com.example.sell_lp.controller.admin;

import com.example.sell_lp.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.text.ParseException;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminPageController {
    @GetMapping({"", "/", "/index"})
    public String adminIndex(Model model) {

        model.addAttribute("pageTitle", "Admin Dashboard - SellLP");
        return "admin/index";
    }
}