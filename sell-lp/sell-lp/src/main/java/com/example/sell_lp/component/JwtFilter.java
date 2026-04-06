package com.example.sell_lp.component;

import com.example.sell_lp.service.authentication.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.GrantedAuthority;
import java.io.IOException;
import java.text.ParseException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;




@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class JwtFilter extends OncePerRequestFilter {

    Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        boolean isPublicPage = requestURI.equals("/login") ||
                requestURI.equals("/introduction") ||
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/images/");
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {

                    String token = cookie.getValue();

                    String username;
                    try {
                        if (!authenticationService.isTokenValid(token)) {
                            logger.warn("Token hết hạn - Xóa cookie");
                            Cookie deleteCookie = new Cookie("jwt", null);
                            deleteCookie.setMaxAge(0);
                            deleteCookie.setPath("/");
                            if(!isPublicPage) {
                                response.addCookie(deleteCookie);
                                response.sendRedirect("/login?error=expired");
                                return;
                            }
                        }
                        username = authenticationService.extractUsernameFromToken(token);
                    } catch (ParseException | JOSEException e) {
                        logger.warn("Lỗi xác thực: " + e.getMessage());
                        continue;
                    } catch (Exception e) {
                        logger.error("Unexpected error processing JWT token: " + e.getMessage());
                        continue;
                    }

                    if (username != null) {

                        try {
                            SignedJWT signedJWT = SignedJWT.parse(token);
                            String rolesClaim = signedJWT.getJWTClaimsSet().getStringClaim("role");

                            java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();
                            if (rolesClaim != null && !rolesClaim.isEmpty()) {
                                for (String role : rolesClaim.split(" ")) {
                                    authorities.add(new SimpleGrantedAuthority(role));
                                }
                            }

                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(username, null, authorities);

                            SecurityContextHolder.getContext().setAuthentication(auth);
                        } catch (Exception e) {
                            logger.error("Lỗi trích xuất Role từ Token: " + e.getMessage());
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
