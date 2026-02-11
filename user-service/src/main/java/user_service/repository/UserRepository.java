package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import user_service.entity.UserEntity;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // USE CASE: Unique Identity Check
    // Used during signup or profile updates to ensure email isn't duplicated
    Optional<UserEntity> findByEmail(String email);
}