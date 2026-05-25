package com.example.sell_lp.repository.user;

import com.example.sell_lp.entity.Role;
import com.example.sell_lp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    // Thêm hàm này vào UserRepository của bạn
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName IN :roleNames AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsersByRoles(@Param("keyword") String keyword,
                                  @Param("roleNames") Set<String> roleNames,
                                  Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName IN :roleNames")
    Page<User> findAllByRoles(@Param("roleNames") Set<String> roleNames, Pageable pageable);
}
