package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.LoginRequest;
import com.example.rentcarkg.dto.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest registerRequest);
    String login(LoginRequest request);
}
