package auth_service.service;


import auth_service.entity.UserCredential;
import auth_service.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // USE CASE: Registration
    public String saveUser(UserCredential credential) {
        // Encrypt the password before saving to DB
        credential.setPassword(passwordEncoder.encode(credential.getPassword()));
        repository.save(credential);
        return "User added to the system successfully!";
    }

    // USE CASE: Generating Token
    public String generateToken(String username) {
        return jwtService.generateToken(username);
    }

    // USE CASE: Validation (Called by API Gateway)
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
