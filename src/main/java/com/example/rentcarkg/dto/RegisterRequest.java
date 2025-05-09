package com.example.rentcarkg.dto;

import com.example.rentcarkg.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 6 characters")
        String password,

        Role role
) {
    public Role role() {
        return role != null ? role : Role.CLIENT; // Устанавливаем роль по умолчанию
    }
}

