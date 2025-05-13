package com.example.rentcarkg.controller;

import com.example.rentcarkg.dto.AuthResponse;
import com.example.rentcarkg.dto.LoginRequest;
import com.example.rentcarkg.dto.RefreshTokenRequest;
import com.example.rentcarkg.dto.RegisterRequest;
import com.example.rentcarkg.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Аутентификация", description = "Регистрация и вход пользователей")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendResetPasswordToken(email);
        return ResponseEntity.ok("Письмо для сброса пароля отправлено");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Пароль успешно сброшен");
    }

//    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*",
//            methods = {RequestMethod.GET, RequestMethod.OPTIONS})
    @Operation(summary = "Обновление JWT токена")
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }
}
