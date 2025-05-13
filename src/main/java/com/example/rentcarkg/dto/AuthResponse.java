package com.example.rentcarkg.dto;

public record AuthResponse(String token, String refreshToken, long expiresIn) {
}


