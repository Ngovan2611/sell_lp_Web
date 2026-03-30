package com.example.sell_lp.component;

import com.example.sell_lp.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {

                    String token = cookie.getValue();

                    String username;
                    try {
                        username = authenticationService.extractUsernameFromToken(token);
                    } catch (ParseException | JOSEException e) {
                        logger.warn("Invalid JWT token: " + e.getMessage());
                        continue;
                    } catch (Exception e) {
                        logger.error("Unexpected error processing JWT token: " + e.getMessage());
                        continue;
                    }

                    if (username != null) {

                        try {
                            com.nimbusds.jwt.SignedJWT signedJWT = com.nimbusds.jwt.SignedJWT.parse(token);
                            String rolesClaim = signedJWT.getJWTClaimsSet().getStringClaim("role");

                            java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
                            if (rolesClaim != null && !rolesClaim.isEmpty()) {
                                for (String role : rolesClaim.split(" ")) {
                                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
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
