package com.example.userservice.controller;

import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return service.createUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        // You might want to remove this or restrict it
        return service.searchByRole("STUDENT");
    }

    @PostMapping("/signin-check")
    public ResponseEntity<String> signinCheck(@RequestParam String email) {
        service.verifyUserActive(email);
        return ResponseEntity.ok("User is valid");
    }

    @GetMapping("/role/{roleName}")
    public List<User> getUsersByRole(@PathVariable String roleName) {
        return service.searchByRole(roleName);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        service.initiatePasswordReset(email);
        return ResponseEntity.ok("Reset instructions sent.");
    }

    // Add other endpoints as needed...
}