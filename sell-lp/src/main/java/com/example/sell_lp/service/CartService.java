package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.CartCreationRequest;
import com.example.sell_lp.entity.Cart;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.enums.Status;
import com.example.sell_lp.mapper.CartMapper;
import com.example.sell_lp.repository.CartRepository;
import com.example.sell_lp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;


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
