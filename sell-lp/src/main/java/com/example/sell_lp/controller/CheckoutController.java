package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.AddressResponse;
import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.service.AddressService;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class CheckoutController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AddressService addressService;

    @Autowired
    private CartItemService cartItemService;

    @GetMapping("/checkout")
    public String checkout(Model model,
                           @CookieValue(value = "jwt", required = false) String token,
                           @RequestParam(value = "ids", required = false) String ids)
            throws Exception {

        String username = authenticationService.extractUsernameFromToken(token);
        if(username == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", username);

        List<AddressResponse> addresses = addressService.getAllAddressesByUsername(username);
        model.addAttribute("addresses", addresses);

        AddressResponse address = addresses.isEmpty() ? null : addresses.get(0);
        model.addAttribute("address", address);

        if(ids != null && !ids.isEmpty()) {
            String[] idArray = ids.split(",");
            List<CartItemResponse> selectedItems = cartItemService.getCartItemsByIds(Arrays.asList(idArray));
            model.addAttribute("items", selectedItems);

            double total = selectedItems.stream()
                    .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                    .sum();
            model.addAttribute("total", total);
            model.addAttribute("ids", ids);
        } else {
            model.addAttribute("total", 0.0);
            model.addAttribute("ids", "");
        }

        return "checkout";
    }
}
