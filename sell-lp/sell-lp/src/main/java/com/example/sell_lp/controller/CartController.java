package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.service.cart.CartItemService;
import com.example.sell_lp.service.cart.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
@PreAuthorize("isAuthenticated()")
public class CartController {
    CartItemService cartItemService;
    CartService cartService;

    @GetMapping("/cart")
    public String getCart(
            Model model,
            Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();

        var cart = cartService.getOrCreateCart(username);
        List<CartItemResponse> cartItems = cartItemService.getCartItemsByCartId(cart.getCartId());
        List<CartItemResponse> reversedCartItem = new ArrayList<>(cartItems);
        Collections.reverse(reversedCartItem);
        double total = cartItems.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();
        model.addAttribute("totalPrice", total);
        model.addAttribute("cartItems", reversedCartItem);
        model.addAttribute("username", username);
        return "cart";
    }


    @DeleteMapping("/cart/delete/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> deleteCartItem(
            @PathVariable Long cartItemId, Principal principal) {

        String username = principal.getName();
        if(username == null) {
            return ResponseEntity.status(401).body("Không thể xử lý");
        }

        cartItemService.deleteCartItem(cartItemId, username);

        return ResponseEntity.ok("Cart item deleted");
    }


    @PostMapping("/cart/update/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            Principal principal) {

        String username = principal.getName();
        if (username == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            cartItemService.updateQuantity(cartItemId, quantity, username);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
