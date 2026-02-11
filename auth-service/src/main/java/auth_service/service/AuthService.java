package auth_service.service;
//
import auth_service.entity.UserCredential;
import auth_service.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthService(UserCredentialRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }


    public String saveUser(UserCredential credential) {
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return "user added to the system";
    }

    // Fixes: "cannot find symbol method generateToken"
    public String generateToken(String username) {
        // Fetch user to get their Tenant ID
        UserCredential user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Pass the tenantId to the JWT generator
        return jwtService.generateToken(username, user.getTenantId());
    }


    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public UserCredential createUser(UserCredential user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public void verifyUserActive(String email) {
        UserCredential user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isActive()) {
            throw new RuntimeException("User account is currently inactive");
        }
    }

    public UserCredential getUserById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserCredential updateUser(int id, UserCredential userDetails) {
        UserCredential user = getUserById(id);
        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        return repository.save(user);
    }

    public List<UserCredential> searchByRole(String roleName) {
        return repository.findAll().stream()
                .filter(user -> user.getRoleName().equalsIgnoreCase(roleName))
                .collect(Collectors.toList());
    }

    public void initiatePasswordReset(String email) {
        UserCredential user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        repository.save(user);
        System.out.println("Generated Reset Token for " + email + ": " + token);
    }

    public void resetPassword(String token, String newPassword) {
        UserCredential user = repository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        repository.save(user);
    }

    public void deleteUser(int id) {
        UserCredential user = getUserById(id);
        user.setActive(false);
        repository.save(user);
    }
}