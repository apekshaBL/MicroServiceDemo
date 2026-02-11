package auth_service.controller;


import auth_service.entity.UserCredential;
import auth_service.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final AuthService service;

    public UserController(AuthService service) {
        this.service = service;
    }

    // USE CASE: Create New Profile (Admin usage)
    @PostMapping
    public ResponseEntity<UserCredential> createProfile(@RequestBody UserCredential user) {
        return new ResponseEntity<>(service.createUser(user), HttpStatus.CREATED);
    }

    // USE CASE: Signup (Self-registration)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserCredential user) {
        service.createUser(user);
        return ResponseEntity.ok("User registered successfully. Please sign in.");
    }

    // USE CASE: Signin Check
    @PostMapping("/signin-check")
    public ResponseEntity<String> signinCheck(@RequestParam String email) {
        service.verifyUserActive(email);
        return ResponseEntity.ok("User is valid and active");
    }

    // USE CASE: Get My Profile
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserCredential> getProfile(@PathVariable int id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    // USE CASE: Update Profile (User Edit)
    @PutMapping("/profile/{id}")
    public ResponseEntity<UserCredential> updateProfile(@PathVariable int id, @RequestBody UserCredential user) {
        return ResponseEntity.ok(service.updateUser(id, user));
    }

    // USE CASE: Role-Based Access
    @GetMapping("/role/{roleName}")
    public List<UserCredential> getUsersByRole(@PathVariable String roleName) {
        return service.searchByRole(roleName);
    }

    // USE CASE: Forgot Password
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

    // USE CASE: Delete Profile (Soft Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable int id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}