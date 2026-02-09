package com.example.userservice.controller;
import com.example.common.multitenancy.TenantContext; // <--- Import this
import com.example.userservice.entity.User;
import com.example.userservice.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService service, AuthenticationManager authenticationManager) {
        this.service = service;
        this.authenticationManager = authenticationManager;
    }

    // UPDATED: Now accepts tenantId to know WHERE to save the user
    @PostMapping("/register")
    public String addNewUser(@RequestBody User user, @RequestParam String tenantId) {
        // 1. Switch to the correct schema
        TenantContext.setCurrentTenant(tenantId);

        try {
            // 2. Save user (now it will go to 'engineering.users')
            return service.saveUser(user);
        } finally {
            // 3. Always clean up!
            TenantContext.clear();
        }
    }

    @PostMapping("/token")
    public String getToken(@RequestBody User authRequest, @RequestParam String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            if (authenticate.isAuthenticated()) {
                return service.generateToken(authRequest.getUsername(), tenantId);
            } else {
                throw new RuntimeException("Invalid Access");
            }
        } finally {
            TenantContext.clear();
        }
    }
}