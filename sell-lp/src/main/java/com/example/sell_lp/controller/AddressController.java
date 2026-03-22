package com.example.sell_lp.controller;


import com.example.sell_lp.dto.request.AddressCreationRequest;
import com.example.sell_lp.dto.request.AddressUpdateRequest;
import com.example.sell_lp.dto.response.AddressResponse;
import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.dto.response.UserResponse;
import com.example.sell_lp.service.AddressService;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CartItemService;
import com.example.sell_lp.service.DistanceService;
import com.example.sell_lp.service.UserService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
public class AddressController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("address/create")
    public String createAddress(@ModelAttribute AddressCreationRequest addressCreationRequest,
                                Model model, @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {

        String username = authenticationService.extractUsernameFromToken(token);
        if(username == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", username);
        addressService.saveAddress(addressCreationRequest);

        return "redirect:/checkout";

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
