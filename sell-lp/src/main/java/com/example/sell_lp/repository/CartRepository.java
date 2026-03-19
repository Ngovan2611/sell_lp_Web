package com.example.sell_lp.repository;

import com.example.sell_lp.entity.Cart;
import com.example.sell_lp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {
    public Cart findByUserAndStatus(User user, String status);

}
