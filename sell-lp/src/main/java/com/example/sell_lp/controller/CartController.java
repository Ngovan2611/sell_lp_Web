package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CartItemService;
import com.example.sell_lp.service.CartService;
import com.example.sell_lp.service.CategoryService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class CartController {
    AuthenticationService authenticationService;
    CartItemService cartItemService;
    CartService cartService;
    CategoryService categoryService;

    @GetMapping("/cart")
    public String getCart(
            Model model,
            @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {

        String username = authenticationService.extractUsernameFromToken(token);
        if(username == null) {
            return "redirect:/login";
        }

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
            @PathVariable Long cartItemId,
            @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {

        String username = authenticationService.extractUsernameFromToken(token);
        if(username == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        cartItemService.deleteCartItem(cartItemId);

        return ResponseEntity.ok("Cart item deleted");
    }


    @PostMapping("/cart/update/{cartItemId}")
    @ResponseBody
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {

        String username = authenticationService.extractUsernameFromToken(token);
        if (username == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        try {
            cartItemService.updateQuantity(cartItemId, quantity);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
