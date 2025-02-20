package com.example.rentcarkg.service;

import com.example.rentcarkg.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${token.signing.key}")
    private String SECRET_KEY;
    private static final long EXPIRATION_TIME = 864000000; // 24 часа

    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name()) // добавляем роль в токен
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }
}
