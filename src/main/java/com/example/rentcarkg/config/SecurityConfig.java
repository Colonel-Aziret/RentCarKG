package com.example.rentcarkg.config;

import com.example.rentcarkg.service.CustomUserDetailsService;
import com.example.rentcarkg.service.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Открываем доступ к статическим файлам
                        .requestMatchers("/static/images/**").permitAll()

                        // 🔓 Открываем доступ к главной странице
                        .requestMatchers("/").permitAll()

                        // 🔓 Открываем доступ к регистрации и авторизации
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/forgot-password", "/api/auth/reset-password", "/api/auth/refresh-token").permitAll()

                        // 🔓 Открываем доступ к Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 🔓 Открытый доступ к email-сервису (без авторизации)
                        .requestMatchers(HttpMethod.POST, "/api/email/send", "/api/email/contact").permitAll()

                        // 🔓 Открытый доступ ко всем GET-запросам на /api/cars/**
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()

                        // 🔓 Открытый доступ ко всем GET-запросам на /api/locations/**
                        .requestMatchers(HttpMethod.GET, "/api/locations/**").permitAll()

                        // 🔐 Владельцы могут добавлять, обновлять и удалять свои машины
                        .requestMatchers(HttpMethod.POST, "/api/cars/add-car").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PUT, "/api/cars/update-car/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/delete-car/**").hasRole("OWNER")

                        // 🔐 Админ доступ к /api/admin/**
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/bookings/owner-requests").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/confirm").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/reject").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/*/cancel").hasRole("CLIENT")

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

