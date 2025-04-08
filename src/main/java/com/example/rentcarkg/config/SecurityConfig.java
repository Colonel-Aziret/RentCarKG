package com.example.rentcarkg.config;

import com.example.rentcarkg.service.CustomUserDetailsService;
import com.example.rentcarkg.service.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Открываем доступ к Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 🔓 Открытый доступ к email-сервису (без авторизации)
                        .requestMatchers(HttpMethod.POST, "/api/email/send").permitAll()

                        // 🔓 Открытый доступ ко всем GET-запросам на /api/cars/**
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()

                        // 🔐 Владельцы могут добавлять, обновлять и удалять свои машины
                        .requestMatchers(HttpMethod.POST, "/api/cars/add-car").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/cars/update-car/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/delete-car/**").hasRole("OWNER")

                        // 🔐 Админ доступ к /api/admin/**
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 🔐 Клиенты могут делать бронирования
                        .requestMatchers("/api/bookings/**").hasRole("CLIENT")

                        // Все остальные запросы требуют авторизации
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

