package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.service.user.UserService;
import com.example.sell_lp.service.otp.EmailService;
import com.example.sell_lp.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AuthController {
    private final EmailService emailService;
    private final OtpService otpService;
    private final UserService userService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        UserResponse existedUser = userService.getUserByEmail(email);
        if(existedUser != null) {
            return ResponseEntity.status(403).body("Email đã tồn tại!");
        }
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return ResponseEntity.ok("Mã đã được gửi đến email của bạn.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verify(@RequestParam String email,
                                         @RequestParam String otp) {
        if (otpService.validateOtp(email, otp)) {
            return ResponseEntity.ok("Xác minh thành công!");
        }
        return ResponseEntity.status(400).body("Mã xác minh không chính xác hoặc đã hết hạn.");
    }
}
