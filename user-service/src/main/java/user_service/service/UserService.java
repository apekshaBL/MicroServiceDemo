package user_service.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import user_service.entity.UserEntity;
import user_service.repository.UserRepository;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repo) {
        this.repo = repo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // USE CASE: Create/Signup with Duplicate Check
    public UserEntity createUser(UserEntity user) {
        if(repo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists in this tenant!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null) user.setRole("ROLE_USER"); // Default role
        return repo.save(user);
    }

    public void verifyUserActive(String email) {
        repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserEntity getUserById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public UserEntity updateUser(Long id, UserEntity details) {
        UserEntity user = getUserById(id);
        user.setName(details.getName());
        user.setEmail(details.getEmail());
        return repo.save(user);
    }

    // USE CASE: Forgot Password Logic
    public void initiatePasswordReset(String email) {
        UserEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));
        // In a real app, generate a token, save to DB with expiry, and send email
        String tempToken = UUID.randomUUID().toString();
        System.out.println("DEBUG: Reset link for " + email + " is: /reset-password?token=" + tempToken);
    }

    public void resetPassword(String token, String newPassword) {
        // In a real app, validate the token from a 'ResetToken' table
        // For now, we update password by email (logic simplified for demo)
        System.out.println("Password reset with token: " + token);
    }

    public List<UserEntity> searchByRole(String role) {
        return repo.findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .toList();
    }

    public void deleteUser(Long id) {
        repo.deleteById(id);
    }
}