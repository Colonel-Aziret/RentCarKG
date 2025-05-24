package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.response.AuthResponse;
import com.example.rentcarkg.dto.request.LoginRequest;
import com.example.rentcarkg.dto.request.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    void sendResetPasswordToken(String email);

    void resetPassword(String token, String newPassword);
}
