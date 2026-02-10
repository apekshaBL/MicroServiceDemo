package user_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import user_service.entity.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> { }
