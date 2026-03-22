package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.CartItemCreationRequest;
import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.entity.CartItem;
import com.example.sell_lp.mapper.CartItemMapper;
import com.example.sell_lp.repository.CartItemRepository;
import com.example.sell_lp.repository.CartRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService {
    CartItemRepository cartItemRepository;

    CartItemMapper cartItemMapper;

    CartRepository cartRepository;

    ProductVariantService productVariantService;

    public void createCartItem(CartItemCreationRequest request) {
        CartItem cartItem = cartItemMapper.toCartItem(request);
        cartItemRepository.save(cartItem);
    }

    public List<CartItemResponse> getCartItemsByCartId(Integer cartId) {
        return cartItemRepository.findByCart_CartId(cartId).stream()
                .map(cartItemMapper::toCartItemResponse)
                .toList();
    }

    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public void addOrUpdateCartItem(Integer cartId, Long variantId, Integer quantity) {
        CartItem existingItem = cartItemRepository
                .findByCart_CartIdAndVariant_VariantId(cartId, variantId);

        int stock = productVariantService.getVariantEntityById(variantId).getStockQty();

        if (existingItem != null) {
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
            newItem.setVariant(productVariantService.getVariantEntityById(variantId));
            newItem.setQuantity(quantity);
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
        // Chuyển List<String> sang List<Long>
        List<Long> ids = cartItemIds.stream()
                .map(Long::parseLong)
                .toList();

        // Query DB theo id
        List<CartItem> items = cartItemRepository.findAllById(ids);

        // Map sang DTO
        return items.stream()
                .map(cartItemMapper::toCartItemResponse)
                .toList();
    }
}
