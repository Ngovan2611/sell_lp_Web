package com.example.sell_lp.repository;

import com.example.sell_lp.dto.request.UserCreationRequest;
import com.example.sell_lp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public User findByUsername(String username);
    public User findByUserId(String userId);
}
