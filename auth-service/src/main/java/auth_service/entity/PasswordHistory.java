package auth_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_history")
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredential user;

    @Column(nullable = false)
    private String password;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    // Standard Empty Constructor (Required by JPA/Hibernate)
    public PasswordHistory() {
    }

    // Standard All-Args Constructor (Optional, but helpful)
    public PasswordHistory(UserCredential user, String password, LocalDateTime changedAt) {
        this.user = user;
        this.password = password;
        this.changedAt = changedAt;
    }

    // --- Keep the manual Getters and Setters you already wrote below ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserCredential getUser() { return user; }
    public void setUser(UserCredential user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}