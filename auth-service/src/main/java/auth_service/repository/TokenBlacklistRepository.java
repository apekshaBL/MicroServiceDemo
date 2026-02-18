package auth_service.repository;

import auth_service.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Integer> {
    boolean existsByToken(String token);
}