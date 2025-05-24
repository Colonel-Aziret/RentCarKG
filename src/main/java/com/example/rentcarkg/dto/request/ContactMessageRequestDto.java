package com.example.rentcarkg.dto.request;

public record ContactMessageRequestDto(
        String fullName, String email, String message
) {
}
