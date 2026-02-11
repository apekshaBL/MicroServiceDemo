package auth_service.controller;

//
import auth_service.dto.AuthRequest;
import auth_service.entity.UserCredential;
import auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 1. REGISTER (Public)
    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential user) {
        return service.saveUser(user);
    }

    // 2. LOGIN / GET TOKEN (Public)
    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        // This validates the user/password with the DB
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authenticate.isAuthenticated()) {
            // If valid, generate the JWT
            return service.generateToken(authRequest.getUsername());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    // 3. VALIDATE TOKEN (Used by Gateway)
    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return "Token is valid";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        service.initiatePasswordReset(email);
        return ResponseEntity.ok("Reset instructions sent to your email");
    }

    // USE CASE: Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        service.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset successfully");
    }
}