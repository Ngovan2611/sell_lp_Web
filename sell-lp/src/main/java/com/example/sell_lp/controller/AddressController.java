package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.AddressCreationRequest;
import com.example.sell_lp.dto.request.AddressUpdateRequest;
import com.example.sell_lp.dto.response.AddressResponse;

import com.example.sell_lp.service.AddressService;
import com.example.sell_lp.service.AuthenticationService;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;



@Controller
public class AddressController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/address/create")
    @ResponseBody
    public ResponseEntity<?> createAddress(@ModelAttribute AddressCreationRequest request,
                                           @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {

        String username = authenticationService.extractUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        }

        try {
            // Lưu địa chỉ và trả về DTO để JS dùng
            AddressResponse address = addressService.saveAddress(request);
            return ResponseEntity.ok(address); // trả JSON
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }



    @PostMapping("/address/edit")
    public ResponseEntity<?> editAddress(@ModelAttribute AddressUpdateRequest request,
                              @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {
        String username = authenticationService.extractUsernameFromToken(token);
        if(username == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            addressService.updateAddress(request, username);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
