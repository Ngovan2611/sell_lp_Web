package com.example.sell_lp.service;


import com.example.sell_lp.entity.Cart;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.enums.Status;
import com.example.sell_lp.repository.CartRepository;
import com.example.sell_lp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;


    UserRepository userRepository;


    public Cart getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Cart existingCart = cartRepository.findByUserAndStatus(user, Status.ACTIVE.name());
        if (existingCart != null) {
            return existingCart;
        }

        Cart newCart = Cart.builder()
                .user(user)
                .status(Status.ACTIVE.name())
                .build();

        return cartRepository.save(newCart);
    }
}
