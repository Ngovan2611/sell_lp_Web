package com.example.sell_lp.controller;

import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CartItemService;
import com.example.sell_lp.service.CartService;
import com.example.sell_lp.service.CategoryService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CategoryService categoryService;

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
        List<CategoryResponse> categories = categoryService.findAll();
        double total = cartItems.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        model.addAttribute("totalPrice", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("username", username);
        model.addAttribute("categories", categories);

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


//    @GetMapping("/cart/totals")
//    @ResponseBody
//    public ResponseEntity<?> getCartTotals(
//            @CookieValue(value = "jwt", required = false) String token)
//            throws ParseException, JOSEException {
//
//        String username = authenticationService.extractUsernameFromToken(token);
//        if(username == null) {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }
//
//        var cart = cartService.getOrCreateCart(username);
//        List<CartItemResponse> cartItems = cartItemService.getCartItemsByCartId(cart.getCartId());
//
//        int totalQuantity = cartItems.stream()
//                .mapToInt(i -> i.getQuantity())
//                .sum();
//
//        double totalPrice = cartItems.stream()
//                .mapToDouble(i -> i.getQuantity() * i.getUnitPrice())
//                .sum();
//
//        return ResponseEntity.ok(new CartTotalsResponse(totalQuantity, totalPrice));
//    }
//
//    public static class CartTotalsResponse {
//        private int totalQuantity;
//        private double totalPrice;
//
//        public CartTotalsResponse(int totalQuantity, double totalPrice) {
//            this.totalQuantity = totalQuantity;
//            this.totalPrice = totalPrice;
//        }
//
//        public int getTotalQuantity() {
//            return totalQuantity;
//        }
//
//        public double getTotalPrice() {
//            return totalPrice;
//        }
//    }
}
