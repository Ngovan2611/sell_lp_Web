//package com.example.sell_lp.component;
//
//import com.example.sell_lp.entity.User;
//import com.example.sell_lp.enums.Role;
//import com.example.sell_lp.repository.UserRepository;
//import com.example.sell_lp.service.AuthenticationService;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.Date;
//
//@Component
//public class GoogleLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private AuthenticationService authenticationService;
//
//    @Override
//    public void onAuthenticationSuccess(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Authentication authentication) throws IOException {
//
//        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//
//        String email = oauthUser.getAttribute("email");
//        String name = oauthUser.getAttribute("name");
//
//        User user = userRepository.findByEmail(email);
//
//        if (user == null) {
//
//            user = User.builder()
//                    .username(email.substring(0, email.indexOf("@")))
//                    .email(email)
//                    .fullName(name)
//
//                    .role(Role.USER.name())
//                    .isActive(true)
//                    .createdAt(new Date())
//                    .build();
//
//            userRepository.save(user);
//        }
//
//        // tạo JWT
//        String token = authenticationService.generateToken(user);
//
//        Cookie cookie = new Cookie("jwt", token);
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(86400);
//
//        response.addCookie(cookie);
//
//        response.sendRedirect("/home");
//    }
//}
