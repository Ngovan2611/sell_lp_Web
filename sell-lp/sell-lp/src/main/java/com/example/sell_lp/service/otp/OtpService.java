package com.example.sell_lp.service.otp;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public String generateOtp(String email) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000); // Tạo mã 6 số
        otpStorage.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String userInputOtp) {
        return userInputOtp.equals(otpStorage.get(email));
    }
}