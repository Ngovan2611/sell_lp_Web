package com.example.sell_lp.config;

import com.example.sell_lp.component.SocialLoginSuccessHandler;
import com.example.sell_lp.component.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig {


    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    SocialLoginSuccessHandler socialLoginSuccessHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

     http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())

             .authorizeHttpRequests(auth -> auth
                     .requestMatchers("/login", "/introduction", "/register", "/home", "/css/**", "/images/**", "/js/**").permitAll()
                     .requestMatchers("/profile/**","/order/**", "/address/**", "/buy-now", "/cart/**", "/history-order/**", "/change-password").hasRole("USER")
                     .requestMatchers("/admin/**").hasRole("ADMIN")

                     .anyRequest().authenticated()
             )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("jwt")
                )


             .oauth2Login(oauth -> oauth
                     .loginPage("/login")
                     .successHandler(socialLoginSuccessHandler)
             )
             .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
