package com.example.sell_lp.service.authentication;


import com.example.sell_lp.dto.request.AuthenticationRequest;
import com.example.sell_lp.dto.response.AuthenticationResponse;
import com.example.sell_lp.entity.User;
import com.example.sell_lp.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class AuthenticationService {
    protected static final String KEY = "9KfT7sP3aV8xL2mN5qR4wE1yZ6uD0cHjB3nX7pQ8tM5sK1vL9aW2eR4yT6uI8oP0";

    UserRepository userRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {

        var user = userRepository.findByUsername(authenticationRequest.getUsername());

        if(user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        if(!user.isActive()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }
        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }



    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("NVV")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.MINUTES).toEpochMilli()
                ))
                .claim("role", buildRole(user))
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

    private String buildRole(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> joiner.add("ROLE_" + role.getRoleName()));
        }
        return joiner.toString();
    }
    public boolean isTokenValid(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
