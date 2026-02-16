package user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.entity.User;
import user_service.service.UserService;

@RestController
@RequestMapping("")
public class UserController {
    @Autowired private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(userService.getProfile(email));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<User> updateProfile(@RequestHeader("X-User-Email") String email,
                                              @RequestParam String username) {
        return ResponseEntity.ok(userService.updateProfile(email, username));
    }
}