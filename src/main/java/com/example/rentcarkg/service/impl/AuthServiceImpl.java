package com.example.rentcarkg.service.impl;

import com.example.rentcarkg.dto.response.AuthResponse;
import com.example.rentcarkg.dto.request.LoginRequest;
import com.example.rentcarkg.dto.request.RegisterRequest;
import com.example.rentcarkg.exceptions.CustomTokenExpiredException;
import com.example.rentcarkg.exceptions.CustomTokenValidationException;
import com.example.rentcarkg.model.PasswordResetToken;
import com.example.rentcarkg.model.User;
import com.example.rentcarkg.repository.PasswordResetTokenRepository;
import com.example.rentcarkg.repository.UserRepository;
import com.example.rentcarkg.service.AuthService;
import com.example.rentcarkg.service.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JavaMailSender mailSender;
    private final PasswordResetTokenRepository tokenRepository;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role()
        );

        userRepository.save(user);

        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Failed login attempt for email: {}", request.email());
            throw new BadCredentialsException("Invalid password");
        }

        log.info("User '{}' logged in with role: {}", user.getEmail(), user.getRole());
        return generateAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            String email = jwtProvider.getEmailFromToken(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new AuthResponse(
                    jwtProvider.generateToken(user),
                    jwtProvider.generateRefreshToken(user),
                    user.getRole().name(),
                    user.getEmail(),
                    jwtProvider.getExpirationTime()
            );
        } catch (CustomTokenExpiredException | ExpiredJwtException e) {
            throw new CustomTokenValidationException("Refresh token expired");
        } catch (JwtException e) {
            throw new CustomTokenValidationException("Invalid refresh token");
        }
    }

    private AuthResponse generateAuthResponse(User user) {
        String token = jwtProvider.generateToken(user);
        long expiresIn = jwtProvider.getExpirationTime();

        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new AuthResponse(
                token,
                refreshToken,
                user.getRole().name(),
                user.getEmail(),
                expiresIn
        );
    }

    public void sendResetPasswordToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                null,
                token,
                user,
                LocalDateTime.now().plusHours(1)
        );

        tokenRepository.save(resetToken);

        String link = "http://localhost:3000/reset-password?token=" + token;
        String body = "Для сброса пароля перейдите по ссылке: " + link;

        sendEmail(email, body);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный токен"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Токен истёк");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }

    private void sendEmail(String to, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@rentcarkg.kg");
        message.setTo(to);
        message.setSubject("Сброс пароля");
        message.setText(body);
        mailSender.send(message);
    }
}
