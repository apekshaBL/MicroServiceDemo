package com.example.userservice.service;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {
        return repository.save(user);
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void verifyUserActive(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive()) {
            throw new RuntimeException("User is inactive");
        }
    }

    public List<User> searchByRole(String role) {
        return repository.findByRole(role);
    }

    public void initiatePasswordReset(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setResetToken(UUID.randomUUID().toString());
        repository.save(user);
        // In real app: Send Email here
        System.out.println("Reset Token for " + email + ": " + user.getResetToken());
    }


    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}