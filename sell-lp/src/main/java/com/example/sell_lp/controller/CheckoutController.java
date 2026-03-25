package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.AddressResponse;
import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.service.AddressService;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CartItemService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
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
                           @RequestParam(value = "ids", required = false) String ids) throws Exception {

        String username = authenticationService.extractUsernameFromToken(token);
        if (username == null) return "redirect:/login";

        List<CartItemResponse> selectedItems;
        if (ids != null && !ids.isEmpty()) {
            String[] idArray = ids.split(",");
            selectedItems = cartItemService.getCartItemsByIds(Arrays.asList(idArray));
        } else {
            selectedItems = Collections.emptyList();
        }

        return processCheckout(model, selectedItems, ids, username);
    }

    @GetMapping("/buy-now")
    public String buyNow(@RequestParam("variantId") Long variantId,
                         @RequestParam(value = "qty", defaultValue = "1") int qty,
                         Model model,
                         @CookieValue(value = "jwt", required = false) String token) throws Exception {

        String username = authenticationService.extractUsernameFromToken(token);
        if (username == null) return "redirect:/login";

        // Tạo item tạm thời cho Mua ngay
        CartItemResponse item = cartItemService.createImmediateCheckoutItem(variantId, qty);
        List<CartItemResponse> items = Collections.singletonList(item);

        return processCheckout(model, items, String.valueOf(variantId), username);
    }

    // HÀM DÙNG CHUNG: Đảm bảo luôn có total và addresses
    private String processCheckout(Model model, List<CartItemResponse> items, String ids, String username) {
        model.addAttribute("username", username);
        model.addAttribute("items", items);
        model.addAttribute("ids", ids);

        // 1. Luôn tính Total (Tránh lỗi null + 30000 trong HTML)
        double total = items.stream()
                .mapToDouble(i -> (i.getUnitPrice() != null ? i.getUnitPrice() : 0.0) * i.getQuantity())
                .sum();
        model.addAttribute("total", total);

        // 2. Luôn lấy danh sách địa chỉ
        List<AddressResponse> addresses = addressService.getAllAddressesByUsername(username);
        model.addAttribute("addresses", addresses);
        model.addAttribute("address", addresses.isEmpty() ? null : addresses.get(0));

        return "checkout";
    }
}