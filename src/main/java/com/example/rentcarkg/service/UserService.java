package com.example.rentcarkg.service;

import com.example.rentcarkg.enums.Role;

public interface UserService {
    void updateUserRole(String email, Role newRole);
}
