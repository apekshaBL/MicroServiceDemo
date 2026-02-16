package user_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import user_service.entity.User;
import user_service.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;

    public User getProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in this schema"));
    }

    public User updateProfile(String email, String newUsername) {
        User user = getProfile(email);
        user.setUsername(newUsername);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
}