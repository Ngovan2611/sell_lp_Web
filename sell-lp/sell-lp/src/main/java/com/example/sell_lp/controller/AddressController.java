package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.AddressCreationRequest;
import com.example.sell_lp.dto.request.AddressUpdateRequest;
import com.example.sell_lp.dto.response.AddressResponse;
import com.example.sell_lp.service.address.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("isAuthenticated()")
public class AddressController {
    AddressService addressService;


    @PostMapping("/address/create")
    @ResponseBody
    public ResponseEntity<?> createAddress(@ModelAttribute AddressCreationRequest request,
                                           Principal principal) {

        if (principal == null) {
            ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        }

        try {
            AddressResponse address = addressService.saveAddress(request);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }



    @PostMapping("/address/edit")
    public ResponseEntity<?> editAddress(@ModelAttribute AddressUpdateRequest request,
                                         Principal principal) {
        String username = (String) principal.getName();
        if(username == null) {
            return ResponseEntity.status(401).body("Không thể xử lý...");
        }

        try {
            addressService.updateAddress(request, username);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
