package com.example.rentcarkg.controller;

import com.example.rentcarkg.enums.Role;
import com.example.rentcarkg.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @Operation(
            summary = "Обновить роль пользователя",
            description = "Позволяет администратору изменить роль пользователя по его email. Доступно только ADMIN."
    )
    @PutMapping("/users/{email}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(
            @PathVariable @Parameter(description = "Email пользователя") String email,
            @RequestParam @Parameter(description = "Новая роль (например, OWNER, CLIENT, ADMIN)") String newRole) {

        userService.updateUserRole(email, Role.valueOf(newRole.toUpperCase())); // Конвертируем в enum
        return ResponseEntity.ok("User role updated to " + newRole);
    }

    @Operation(
            summary = "Проверить текущие роли пользователя",
            description = "Возвращает список ролей текущего аутентифицированного пользователя."
    )
    @GetMapping("/check-role")
    public ResponseEntity<String> checkRole(Authentication authentication) {
        return ResponseEntity.ok("Current roles: " + authentication.getAuthorities());
    }
}
