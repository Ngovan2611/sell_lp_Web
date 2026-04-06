package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.dto.response.ProductVariantResponse;
import com.example.sell_lp.entity.Cart;
import com.example.sell_lp.service.cart.CartItemService;
import com.example.sell_lp.service.cart.CartService;
import com.example.sell_lp.service.product.ProductService;
import com.example.sell_lp.service.variant.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductDetailController {
    ProductVariantService productVariantService;

    ProductService productService;


    CartService cartService;

    CartItemService cartItemService;

    @GetMapping("/product/{productId}")
    public String getProductDetail(Model model,
                                   @PathVariable("productId") Long productId,
                                   Principal principal) {
        if(principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();




        ProductResponse product = productService.getProductById(productId);

        List<ProductVariantResponse> productVariant = productVariantService.getAllProductVariantById(productId);

        List<String> colors = productVariant.stream()
                .map(ProductVariantResponse::getColorName)
                .distinct()
                .toList();

        List<Integer> rams = productVariant.stream()
                .map(ProductVariantResponse::getRamSize)
                .distinct()
                .toList();

        List<Integer> roms = productVariant.stream()
                .map(ProductVariantResponse::getRomSize)
                .distinct()
                .toList();

        List<ProductResponse> relatedProducts = productService.
                getRelatedProducts(product.getCategory().getCategoryId(), productId, PageRequest.of(0, 10));

        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("variants", productVariant);
        model.addAttribute("product", product);
        model.addAttribute("colors", colors);
        model.addAttribute("rams", rams);
        model.addAttribute("roms", roms);
        model.addAttribute("username", username);
        return "product-detail";
    }



    @PostMapping("/cart/add")
    @ResponseBody
    public ResponseEntity<?> addToCartAjax(@RequestParam Long variantId,
                                           Principal principal) {

        String username = principal.getName();
        if (username == null) {
            return ResponseEntity.status(401).body("NOT_LOGIN");
        }

        try {
            Cart cart = cartService.getOrCreateCart(username);
            cartItemService.addOrUpdateCartItem(cart.getCartId(), variantId, 1);

            return ResponseEntity.ok("SUCCESS");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
