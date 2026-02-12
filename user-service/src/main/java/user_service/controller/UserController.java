package user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.entity.UserEntity;
import user_service.service.UserService;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @GetMapping("/profile/{id}")
    public ResponseEntity<UserEntity> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserById(id));
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<UserEntity> updateProfile(@PathVariable Long id, @RequestBody UserEntity user) {
        return ResponseEntity.ok(service.updateUser(id, user));
    }


    @GetMapping("/role/{roleName}")
    public List<UserEntity> getUsersByRole(@PathVariable String roleName) {
        return service.searchByRole(roleName);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping
    public ResponseEntity<UserEntity> createProfile(@RequestBody UserEntity user) {
        return new ResponseEntity<>(service.createUser(user), HttpStatus.CREATED);
    }
}