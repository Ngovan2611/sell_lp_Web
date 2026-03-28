package com.example.sell_lp.config;


import com.example.sell_lp.entity.User;
import com.example.sell_lp.enums.Role;
import com.example.sell_lp.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;



@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class ApplicationUnitConfig {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            if(userRepository.findByUsername("admin") == null) {


                User user = new User().builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin1234"))
                        .role(Role.ADMIN.name())
                        .isActive(true)
                        .build();

                userRepository.save(user);
            }
        };
    }
}
