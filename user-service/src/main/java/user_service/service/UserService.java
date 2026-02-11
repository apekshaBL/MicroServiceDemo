package user_service.service;

import org.springframework.stereotype.Service;
import user_service.entity.UserEntity;
import user_service.repository.UserRepository;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public UserEntity createUser(UserEntity user) {
        return repo.save(user);
    }

    public UserEntity getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found for ID: " + id));
    }

    public UserEntity updateUser(Long id, UserEntity details) {
        UserEntity user = getUserById(id);
        user.setName(details.getName());
        user.setEmail(details.getEmail());
        user.setRole(details.getRole());
        return repo.save(user);
    }

    public List<UserEntity> searchByRole(String role) {
        return repo.findAll().stream()
                .filter(u -> u.getRole() != null && u.getRole().equalsIgnoreCase(role))
                .toList();
    }

    public void deleteUser(Long id) {
        repo.deleteById(id);
    }
}