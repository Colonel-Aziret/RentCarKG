package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.AuthResponse;
import com.example.rentcarkg.dto.LoginRequest;
import com.example.rentcarkg.dto.RegisterRequest;
import com.example.rentcarkg.model.User;
import com.example.rentcarkg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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

    private AuthResponse generateAuthResponse(User user) {
        String token = jwtProvider.generateToken(user);
        long expiresIn = jwtProvider.getExpirationTime();

        return new AuthResponse(token, expiresIn);
    }

}
