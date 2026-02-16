package user_service.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    @Column(name = "role_name")
    private String roleName;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}