package com.example.userservice.service;

import com.example.common.security.JwtUtils; // <--- From common-lib
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtService;

    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtUtils jwtService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String saveUser(User user) {
        // Hash the password before saving!
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
        return "User added to system";
    }

    public String generateToken(String username, String tenantId) {
        // Fetch user to get their role
        User user = repository.findByUsername(username).orElseThrow();
        // Use our Common-Lib tool to make the token
        return jwtService.generateToken(username, tenantId, user.getRole());
    }
}