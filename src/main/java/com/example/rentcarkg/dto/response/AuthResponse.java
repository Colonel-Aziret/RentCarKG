package com.example.rentcarkg.dto.response;

public record AuthResponse(String token, String refreshToken, String role, String email, long expiresIn) {
}


