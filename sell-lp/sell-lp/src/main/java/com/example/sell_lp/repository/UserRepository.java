package com.example.sell_lp.repository;

import com.example.sell_lp.entity.Role;
import com.example.sell_lp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByUserId(String userId);
    User findByEmail(String email);
    List<User> findByRoles(Set<Role> roles);
    long count();
}
