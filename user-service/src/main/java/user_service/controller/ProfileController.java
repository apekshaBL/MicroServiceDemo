package user_service.controller;

import user_service.dto.ProfileRequest;
import user_service.entity.UserProfile;
import user_service.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/profile")
public class ProfileController {

    @Autowired
    private ProfileService service;

    @PostMapping("/update")
    public ResponseEntity<String> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String username,
            @RequestBody ProfileRequest request) {
        return ResponseEntity.ok(service.updateProfile(userId, username, request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getMyProfile(@RequestHeader("X-User-Name") String username) {
        return ResponseEntity.ok(service.getProfile(username));
    }
}