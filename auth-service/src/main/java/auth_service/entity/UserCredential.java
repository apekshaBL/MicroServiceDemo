package auth_service.entity;



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_auth") // Removed hardcoded schema to allow multi-tenancy
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; // BCrypt hashed

    // Added for Role-Based Access use case
    private String roleName = "ROLE_USER";

    // Added for Soft Delete use case
    private boolean isActive = true;

    // Added for Forgot Password use case
    private String resetToken;
}
