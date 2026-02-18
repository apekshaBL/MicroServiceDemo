package auth_service.service;

import auth_service.entity.RefreshToken;
import auth_service.entity.UserCredential;
import auth_service.repository.RefreshTokenRepository;
import auth_service.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserCredentialRepository userRepository;

    // Create a new Refresh Token (Rotate old one if exists)
    @Transactional
    public RefreshToken createRefreshToken(String username) {
        // 1. Delete old token to enforce single-session (Optional but safer)
        UserCredential user = userRepository.findByUsername(username).orElseThrow();
        refreshTokenRepository.deleteByUser(user);

        // 2. Create new token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(86400000)) // 24 Hours
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}