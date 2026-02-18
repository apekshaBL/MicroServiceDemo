package auth_service.service;
import auth_service.dto.EmailRequest;
import auth_service.entity.UserCredential;
import auth_service.repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private JwtService jwtService;

    public AuthService(UserCredentialRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- MERGED METHOD START ---
    public String generateToken(String username, String tenantId) {
        UserCredential user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. YOUR LOGIC: Security Check
        if (!user.isActive()) {
            throw new RuntimeException("Account is Locked. Contact Admin.");
        }

        // 2. YOUR LOGIC: Reset Counter on Success
        if (user.getFailedAttempts() > 0) {
            user.setFailedAttempts(0);
            repository.save(user); // Reset to 0
        }

        // 3. HER LOGIC: Pass Role to JWT
        return jwtService.generateToken(username, tenantId, user.getRoleName());
    }
    // --- MERGED METHOD END ---

    public String saveUser(UserCredential user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);

        try {
            EmailRequest email = new EmailRequest();
            email.setTo(user.getEmail());
            email.setSubject("Welcome to " + user.getTenantId() + "!");
            email.setBody("Hello " + user.getUsername() + ",\n\nYour account has been successfully created.\n\nTenant: " + user.getTenantId());
            email.setTenantId(user.getTenantId());

            notificationClient.sendEmail(email);
        } catch (Exception e) {
            System.err.println(" Notification Service Down: " + e.getMessage());
        }

        return "user added to the system";
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

        System.out.println("DEBUG TOKEN: " + token);

        try {
            String resetLink = "http://localhost:8080/reset?token=" + token;
            EmailRequest emailReq = new EmailRequest();
            emailReq.setTo(user.getEmail());
            emailReq.setSubject("Reset Password");
            emailReq.setBody("Click here: " + resetLink);
            emailReq.setTenantId(user.getTenantId());

            notificationClient.sendEmail(emailReq);
        } catch (Exception e) {
            System.err.println("⚠️ Notification Service Down");
        }
    }

    public void resetPassword(String token, String newPassword) {
        UserCredential user = repository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        repository.save(user);

        try {
            EmailRequest email = new EmailRequest();
            email.setTo(user.getEmail());
            email.setSubject("Security Alert: Password Changed");
            email.setBody("Hello " + user.getUsername() + ",\n\nYour password was successfully changed just now.\nIf this wasn't you, contact support immediately.");
            email.setTenantId(user.getTenantId());

            notificationClient.sendEmail(email);
        } catch (Exception e) {
            System.err.println(" Notification Service Down");
        }
    }

    public void deleteUser(int id) {
        UserCredential user = getUserById(id);
        user.setActive(false);
        repository.save(user);
    }

    public void loginFailed(String email) {
        UserCredential user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            return;
        }

        int newFailCount = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailCount);

        System.out.println("DEBUG: Failed attempts for " + email + " is now: " + newFailCount);


        if (newFailCount >= 5) {
            user.setActive(false);


            try {
                EmailRequest emailReq = new EmailRequest();
                emailReq.setTo(user.getEmail());
                emailReq.setSubject("🚨 Security Alert: Account Locked");
                emailReq.setBody("Hello " + user.getUsername() + ",\n\nYour account has been locked due to 5 failed login attempts.\n\nPlease contact support to unlock it.");
                emailReq.setTenantId(user.getTenantId());

                notificationClient.sendEmail(emailReq);
            } catch (Exception e) {
                System.err.println("⚠️ Notification Service Down");
            }
        }

        // Save changes to DB
        repository.save(user);
    }

    public void generateAndSendOTP(String email) {
        UserCredential user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SecureRandom random = new SecureRandom();
        int otpCode = 100000 + random.nextInt(900000);

        user.setResetToken(String.valueOf(otpCode));
        repository.save(user);

        System.out.println("DEBUG OTP: " + otpCode);

        try {
            EmailRequest emailReq = new EmailRequest();
            emailReq.setTo(user.getEmail());
            emailReq.setSubject("Your Secure Login Code");
            emailReq.setBody("Hello " + user.getUsername() + ",\n\nYour One-Time Password (OTP) is: " + otpCode + "\n\nThis code expires in 5 minutes.");
            emailReq.setTenantId(user.getTenantId());

            notificationClient.sendEmail(emailReq);
        } catch (Exception e) {
            System.err.println("⚠️ Notification Service Down: " + e.getMessage());
        }
    }
}