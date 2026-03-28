package com.example.sell_lp.mapper;

import com.example.sell_lp.dto.response.CartItemResponse;
import com.example.sell_lp.entity.Cart;
import com.example.sell_lp.entity.CartItem;

import com.example.sell_lp.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CartItemMapper {



    @Named("mapCart")
    default Cart mapCart(Integer cartId) {
        if(cartId == null) return null;
        Cart cart = new Cart();
        cart.setCartId(cartId);
        return cart;
    }

    @Named("mapVariant")
    default ProductVariant mapVariant(Long variantId) {
        if(variantId == null) return null;
        ProductVariant variant = new ProductVariant();
        variant.setVariantId(variantId);
        return variant;
    }

    CartItemResponse toCartItemResponse(CartItem cartItem);
}
