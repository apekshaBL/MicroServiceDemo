package auth_service.repository;
//
import auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer> {

    // For Sign-in Check and Login (Primary identifier)
    Optional<UserCredential> findByEmail(String email);

    // For User Search and existing username checks
    Optional<UserCredential> findByUsername(String username);

    // For Forgot Password / Reset Password use case
    Optional<UserCredential> findByResetToken(String token);
}