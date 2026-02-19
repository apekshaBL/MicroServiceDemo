package auth_service.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserCredential user;

    public RefreshToken() {}

    public RefreshToken(UserCredential user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    // Manual Builder
    public static RefreshTokenBuilder builder() {
        return new RefreshTokenBuilder();
    }

    public static class RefreshTokenBuilder {
        private UserCredential user;
        private String token;
        private Instant expiryDate;

        public RefreshTokenBuilder user(UserCredential user) {
            this.user = user;
            return this;
        }
        public RefreshTokenBuilder token(String token) {
            this.token = token;
            return this;
        }
        public RefreshTokenBuilder expiryDate(Instant expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }
        public RefreshToken build() {
            return new RefreshToken(user, token, expiryDate);
        }
    }

    // Getters
    public String getToken() { return token; }
    public Instant getExpiryDate() { return expiryDate; }
    public UserCredential getUser() { return user; }
}