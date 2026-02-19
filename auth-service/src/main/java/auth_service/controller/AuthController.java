package auth_service.controller;
import auth_service.common.context.TenantContext;
import auth_service.dto.AuthRequest;
import auth_service.dto.JwtResponse;
import auth_service.dto.RefreshTokenRequest;
import auth_service.entity.RefreshToken;
import auth_service.entity.UserCredential;
import auth_service.service.AuthService;
import auth_service.service.JwtService;
import auth_service.service.RefreshTokenService;
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

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private auth_service.service.LogoutService logoutService;

    @Autowired
    private auth_service.repository.UserCredentialRepository userRepository;

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // <--- Only allows Admins
    public String adminDashboard() {
        return "Welcome Admin! You have authorized access to this secure endpoint.";
    }

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential user) {

        String tenant = (user.getTenantId() != null) ? user.getTenantId() : "public";

        TenantContext.setCurrentTenant(tenant);
        try {
            return service.saveUser(user);
        } finally {
            TenantContext.clear();
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token, @RequestParam String username) {

        String accessToken = token.substring(7);

        logoutService.logout(accessToken, username);

        return "Logged out successfully";
    }

    @PostMapping("/token")
    public JwtResponse getToken(@RequestBody AuthRequest authRequest) {
        String tenant = (authRequest.getTenantId() != null) ? authRequest.getTenantId() : "public";
        TenantContext.setCurrentTenant(tenant);

        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authenticate.isAuthenticated()) {

                String accessToken = service.generateToken(authRequest.getUsername(), tenant);

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());

                return JwtResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .build();
            } else {
                throw new RuntimeException("Invalid Access");
            }
        } finally {
            TenantContext.clear();
        }
    }

    @PostMapping("/refresh")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshRequest, @RequestParam String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        try {
            return refreshTokenService.findByToken(refreshRequest.getToken())
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String accessToken = jwtService.generateToken(user.getUsername(), tenantId, user.getRoleName());
                        return JwtResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshRequest.getToken())
                                .build();
                    }).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        } finally {
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

    @PostMapping("/verify-otp")
    public JwtResponse verifyOtp(@RequestBody auth_service.dto.OtpVerificationRequest request) {
        String tenant = (request.getTenantId() != null) ? request.getTenantId() : "public";
        TenantContext.setCurrentTenant(tenant);

        try {

            String accessToken = service.verifyOtp(request.getEmail(), request.getOtp());

            UserCredential user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User found during OTP check but not found now? This shouldn't happen."));

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());


            return JwtResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .build();
        } finally {
            TenantContext.clear();
        }
    }
}