package auth_service.controller;
import auth_service.common.context.TenantContext;
import auth_service.dto.AuthRequest;
import auth_service.entity.UserCredential;
import auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // <--- Only allows Admins
    public String adminDashboard() {
        return "Welcome Admin! You have authorized access to this secure endpoint.";
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential user) {
        // Default to 'public' if no tenant provided
        String tenant = (user.getTenantId() != null) ? user.getTenantId() : "public";

        TenantContext.setCurrentTenant(tenant);
        try {
            return service.saveUser(user);
        } finally {
            TenantContext.clear();
        }
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest authRequest) {
        String tenant = (authRequest.getTenantId() != null) ? authRequest.getTenantId() : "public";

        // This tells Hibernate to look in the specific schema
        TenantContext.setCurrentTenant(tenant);

        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authenticate.isAuthenticated()) {
                return service.generateToken(authRequest.getUsername(), tenant);
            } else {
                throw new RuntimeException("Invalid Access");
            }
        } finally {
            // Cleanup to prevent memory leaks
            TenantContext.clear();
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        service.validateToken(token);
        return "Token is valid";
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email,
                                                 @RequestParam String tenantId) {

        TenantContext.setCurrentTenant(tenantId);
        try {
            service.initiatePasswordReset(email);
            return ResponseEntity.ok("Reset instructions sent to your email");
        } finally {
            TenantContext.clear();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestBody String newPassword,
                                                @RequestParam String tenantId) {

        TenantContext.setCurrentTenant(tenantId);
        try {
            service.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password has been reset successfully");
        } finally {
            TenantContext.clear();
        }
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, @RequestParam String tenantId) {
        // !!! YOU MUST HAVE THESE LINES !!!
        TenantContext.setCurrentTenant(tenantId);
        System.out.println("DEBUG: Received Request -> Email: " + email + ", Tenant: " + tenantId);


        try {
            service.generateAndSendOTP(email);
            return "OTP sent to your email";
        } finally {
            auth_service.common.context.TenantContext.clear();
        }
    }

    @PostMapping("/simulate-hack")
    public String simulateHack(@RequestParam String email, @RequestParam String tenantId) {

        TenantContext.setCurrentTenant(tenantId);
        try {
            service.loginFailed(email);
            return "Account locked and owner notified";
        } finally {
            TenantContext.clear();
        }
    }
}