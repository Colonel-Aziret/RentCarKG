package com.example.rentcarkg.controller;

import com.example.rentcarkg.enums.Role;
import com.example.rentcarkg.service.UserService;
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

    @PutMapping("/users/{email}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUserRole(@PathVariable String email, @RequestParam String newRole) {
        userService.updateUserRole(email, Role.valueOf(newRole.toUpperCase())); // Явно конвертируем
        return ResponseEntity.ok("User role updated to " + newRole);
    }

    @GetMapping("/check-role")
    public ResponseEntity<String> checkRole(Authentication authentication) {
        return ResponseEntity.ok("Current roles: " + authentication.getAuthorities());
    }
}
