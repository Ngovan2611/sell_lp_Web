package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.CartItemCreationRequest;
import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.dto.response.ProductVariantResponse;
import com.example.sell_lp.entity.CartItem;
import com.example.sell_lp.entity.ProductVariant;
import com.example.sell_lp.mapper.CartItemMapper;
import com.example.sell_lp.mapper.ProductVariantResponseMapper;
import com.example.sell_lp.repository.CartItemRepository;
import com.example.sell_lp.repository.CartRepository;
import com.example.sell_lp.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService {
    CartItemRepository cartItemRepository;

    CartItemMapper cartItemMapper;

    CartRepository cartRepository;

    ProductVariantService productVariantService;

    ProductVariantRepository productVariantRepository;

    ProductVariantResponseMapper productVariantResponseMapper;

    public List<CartItemResponse> getCartItemsByCartId(Integer cartId) {
        return cartItemRepository.findByCart_CartId(cartId).stream()
                .map(cartItemMapper::toCartItemResponse)
                .toList();
    }

    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void addOrUpdateCartItem(Integer cartId, Long variantId, Integer quantity) {

        Optional<CartItem> optional = cartItemRepository
                .findByCart_CartIdAndVariant_VariantId(cartId, variantId);

        ProductVariant variant = productVariantService.getVariantEntityById(variantId);
        int stock = variant.getStockQty();

        if (optional.isPresent()) {

            CartItem existingItem = optional.get();
            int newQty = existingItem.getQuantity() + quantity;

            if (newQty > stock) {
                throw new RuntimeException("Vượt quá số lượng trong kho!");
            }

            existingItem.setQuantity(newQty);
            cartItemRepository.save(existingItem);

        } else {

            if (quantity > stock) {
                throw new RuntimeException("Vượt quá tồn kho");
            }

            CartItem newItem = new CartItem();
            newItem.setCart(cartRepository.findById(cartId).orElseThrow());
            newItem.setVariant(variant);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(productVariantService.getVariantEntityById(variantId).getPrice());
            cartItemRepository.save(newItem);
        }
    }


    public void updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        int stock = cartItem.getVariant().getStockQty();

        if (quantity > stock) {
            throw new RuntimeException("Số lượng vượt quá tồn kho");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    public List<CartItemResponse> getCartItemsByIds(List<String> cartItemIds) {
        List<Long> ids = cartItemIds.stream()
                .map(Long::parseLong)
                .toList();

        List<CartItem> items = cartItemRepository.findAllById(ids);

        return items.stream()
                .map(cartItemMapper::toCartItemResponse)
                .toList();
    }
    public CartItemResponse createImmediateCheckoutItem(Long variantId, int quantity) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        CartItemResponse response = new CartItemResponse();

        response.setVariant(variant);
        response.setQuantity(quantity);
        response.setUnitPrice(variant.getPrice());

        return response;
    }
}
