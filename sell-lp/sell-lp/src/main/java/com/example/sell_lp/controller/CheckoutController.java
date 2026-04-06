package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.AddressResponse;
import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.service.address.AddressService;
import com.example.sell_lp.service.cart.CartItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("isAuthenticated()")
public class CheckoutController {
    AddressService addressService;
    CartItemService cartItemService;

    @GetMapping("/checkout")
    public String checkout(Model model,
                           Principal principal,
                           @RequestParam(value = "ids", required = false) String ids) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();

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
                         Principal principal) {

        String username = principal.getName();
        if (username == null) return "redirect:/login";

        CartItemResponse item = cartItemService.createImmediateCheckoutItem(variantId, qty);
        List<CartItemResponse> items = Collections.singletonList(item);

        return processCheckout(model, items, String.valueOf(variantId), username);
    }



    private String processCheckout(Model model, List<CartItemResponse> items, String ids, String username) {
        model.addAttribute("username", username);
        model.addAttribute("items", items);
        model.addAttribute("ids", ids);

        double total = items.stream()
                .mapToDouble(i -> (i.getUnitPrice() != null ? i.getUnitPrice() : 0.0) * i.getQuantity())
                .sum();
        model.addAttribute("total", total);

        List<AddressResponse> addresses = addressService.getAllAddressesByUsername(username);
        model.addAttribute("addresses", addresses);
        model.addAttribute("address", addresses.isEmpty() ? null : addresses.getFirst());

        return "checkout";
    }
}