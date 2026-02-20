package auth_service.repository;

import auth_service.entity.PasswordHistory;
import auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    // Fetch the last 3 passwords for a user to check against
    List<PasswordHistory> findTop3ByUserOrderByChangedAtDesc(UserCredential user);
}