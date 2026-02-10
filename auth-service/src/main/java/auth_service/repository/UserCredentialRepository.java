package auth_service.repository;


import auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer> {

    // This allows us to find a user in the database using their username
    // Spring Data JPA will automatically write the SQL for this!
    Optional<UserCredential> findByUsername(String username);
}