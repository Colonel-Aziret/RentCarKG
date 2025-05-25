package com.example.rentcarkg.dto.response;

public record AuthResponse(String token, String refreshToken, long expiresIn) {
}


