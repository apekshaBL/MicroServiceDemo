package auth_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public TokenBlacklist() {}

    public TokenBlacklist(String token, LocalDateTime expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

    // Manual Builder
    public static TokenBlacklistBuilder builder() {
        return new TokenBlacklistBuilder();
    }

    public static class TokenBlacklistBuilder {
        private String token;
        private LocalDateTime expiryDate;

        public TokenBlacklistBuilder token(String token) {
            this.token = token;
            return this;
        }
        public TokenBlacklistBuilder expiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }
        public TokenBlacklist build() {
            return new TokenBlacklist(token, expiryDate);
        }
    }
}