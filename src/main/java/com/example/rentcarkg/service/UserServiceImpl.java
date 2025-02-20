package com.example.rentcarkg.service;

import com.example.rentcarkg.enums.Role;
import com.example.rentcarkg.model.User;
import com.example.rentcarkg.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public void updateUserRole(String email, Role newRole) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setRole(newRole);
        userRepository.save(user);
    }
}
