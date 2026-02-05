package user_service.service;


import org.springframework.stereotype.Service;
import user_service.entity.UserEntity;
import user_service.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    public List<UserEntity> getAllUsers() { return repo.findAll(); }
    public UserEntity getUserById(Long id) { return repo.findById(id).orElse(null); }
    public UserEntity createUser(UserEntity user) { return repo.save(user); }
    public void deleteUser(Long id) { repo.deleteById(id); }
}
