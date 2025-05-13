package com.example.rentcarkg.service;

import com.example.rentcarkg.exceptions.CustomTokenExpiredException;
import com.example.rentcarkg.exceptions.CustomTokenValidationException;
import com.example.rentcarkg.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${token.signing.key}")
    private String SECRET_KEY;
    private static final long EXPIRATION_TIME = 86400000; // 24 часа

    public String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            return Jwts.parser()
                    .verifyWith(key)
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 7); // Срок жизни 7 дней
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256) // Используй свой секретный ключ
                .compact();
    }

    public String getRoleFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .setAllowedClockSkewSeconds(60)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (JwtException e) {
            throw new CustomTokenValidationException("Ошибка валидации JWT токена.");
        }
    }

    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }
}
