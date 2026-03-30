package com.example.sell_lp.component;

import com.example.sell_lp.entity.User;
import com.example.sell_lp.repository.UserRepository;
import com.example.sell_lp.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class SocialLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    UserRepository userRepository;

    AuthenticationService authenticationService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String registrationId = request.getRequestURI()
                .split("/oauth2/code/")[1]
                .toUpperCase();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String id = oauthUser.getAttribute("id");

        if (email == null) {
            email = id + "@facebook.com";
        }


        assert name != null;
        String baseUsername = name.toLowerCase().replaceAll("[^a-z0-9]", "");
        String username = baseUsername;

        int i = 1;
        while(userRepository.findByUsername(username) != null) {
            username = baseUsername + i;
            i++;
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {

            user = User.builder()
                    .username(username)
                    .email(email)
                    .fullName(name)
                    .provider(registrationId)
                    .isActive(true)
                    .createdAt(new Date())
                    .build();

            userRepository.save(user);
        }

        String token = authenticationService.generateToken(user);

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);

        response.addCookie(cookie);

        response.sendRedirect("/home");
    }
}
