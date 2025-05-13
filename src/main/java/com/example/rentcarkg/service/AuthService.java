package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.AuthResponse;
import com.example.rentcarkg.dto.LoginRequest;
import com.example.rentcarkg.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    void sendResetPasswordToken(String email);

    void resetPassword(String token, String newPassword);
}
