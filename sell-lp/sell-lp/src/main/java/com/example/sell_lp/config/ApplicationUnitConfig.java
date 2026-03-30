package com.example.sell_lp.config;


import com.example.sell_lp.entity.User;
import com.example.sell_lp.entity.Role;
import com.example.sell_lp.repository.RoleRepository;
import com.example.sell_lp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
public class ApplicationUnitConfig {

    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            if (roleRepository.findByRoleName("ADMIN") == null) {
                roleRepository.save(Role.builder().roleName("ADMIN").build());
            }
            if (roleRepository.findByRoleName("USER") == null) {
                roleRepository.save(Role.builder().roleName("USER").build());
            }

            if (userRepository.findByUsername("admin") == null) {
                Role adminRole = roleRepository.findByRoleName("ADMIN");

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin1234"))
                        .roles(Set.of(adminRole))
                        .isActive(true)
                        .build();

                userRepository.save(user);
                System.out.println("Cấp tài khoản Admin mặc định thành công!");
            }
        };
    }
}