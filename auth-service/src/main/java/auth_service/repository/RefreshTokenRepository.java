package auth_service.repository;

import auth_service.entity.RefreshToken;
import auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(UserCredential user);
}