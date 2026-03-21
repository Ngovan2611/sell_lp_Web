package com.example.sell_lp.controller;


import com.example.sell_lp.dto.response.CategoryResponse;
import com.example.sell_lp.dto.response.ProductResponse;
import com.example.sell_lp.dto.response.ProductVariantResponse;
import com.example.sell_lp.entity.Cart;
import com.example.sell_lp.service.AuthenticationService;
import com.example.sell_lp.service.CartItemService;
import com.example.sell_lp.service.CartService;
import com.example.sell_lp.service.CategoryService;
import com.example.sell_lp.service.ProductService;
import com.example.sell_lp.service.ProductVariantService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductDetailController {
    ProductVariantService productVariantService;

    AuthenticationService authenticationService;

    ProductService productService;

    CategoryService categoryService;

    CartService cartService;

    CartItemService cartItemService;

    @GetMapping("/product/{productId}")
    public String getProductDetail(Model model,
                                   @PathVariable("productId") Long productId,
                                   @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {

        String username = authenticationService.extractUsernameFromToken(token);
        if(username == null) {
            return "redirect:/login";
        }



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

        List<CategoryResponse> categories = categoryService.findAll();
        List<ProductResponse> relatedProducts = productService.
                getRelatedProducts(product.getCategory(), productId, PageRequest.of(0, 10));

        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("categories", categories);
        model.addAttribute("variants", productVariant);
        model.addAttribute("product", product);
        model.addAttribute("colors", colors);
        model.addAttribute("rams", rams);
        model.addAttribute("roms", roms);
        model.addAttribute("username", username);
        return "product-detail";
    }



    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long variantId,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            @CookieValue(value = "jwt", required = false) String token)
            throws ParseException, JOSEException {


        String username = authenticationService.extractUsernameFromToken(token);
        if(username == null) {
            return "redirect:/login";
        }
        Cart cart = cartService.getOrCreateCart(username);
        ProductVariantResponse variantResponse = productVariantService.getVariantById(variantId);
        try {
            cartItemService.addOrUpdateCartItem(cart.getCartId(), variantId, 1);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công");

            model.addAttribute("username", username);
        }catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());

        }
        return "redirect:/product/" + variantResponse.getProductId();
    }

}
