package com.example.sell_lp.repository;

import com.example.sell_lp.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByCart_CartId(Integer cartId);
    CartItem findByCart_CartIdAndVariant_VariantId(Integer cartId, Long variantId);
}
