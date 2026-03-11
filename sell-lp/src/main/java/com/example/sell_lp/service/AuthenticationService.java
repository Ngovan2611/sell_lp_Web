package com.example.sell_lp.service;


import com.example.sell_lp.dto.request.AuthenticationRequest;
import com.example.sell_lp.dto.response.AuthenticationResponse;
import com.example.sell_lp.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AuthenticationService {
    protected static final String KEY = "9KfT7sP3aV8xL2mN5qR4wE1yZ6uD0cHjB3nX7pQ8tM5sK1vL9aW2eR4yT6uI8oP0";
    @Autowired
    private UserRepository userRepository;
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws KeyLengthException {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        var user = userRepository.findByUsername(authenticationRequest.getUsername());
        if(user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        String token = generateToken(authenticationRequest.getUsername());

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }



    private String generateToken(String username) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("NVV")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()
                ))
                .claim("authorities", "ROLE_USER")
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    public String extractUsernameFromToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        return claims.getSubject();
    }

}
