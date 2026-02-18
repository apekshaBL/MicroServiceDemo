package auth_service.service;

import auth_service.entity.TokenBlacklist;
import auth_service.repository.RefreshTokenRepository;
import auth_service.repository.TokenBlacklistRepository;
import auth_service.repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogoutService {

    @Autowired
    private TokenBlacklistRepository blacklistRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserCredentialRepository userRepository;

    @Transactional
    public void logout(String accessToken, String username) {
        // 1. Blacklist the Access Token (so it can't be used immediately)
        TokenBlacklist blacklistEntry = TokenBlacklist.builder()
                .token(accessToken)
                .expiryDate(LocalDateTime.now().plusMinutes(30)) // Match your JWT expiry
                .build();
        blacklistRepository.save(blacklistEntry);

        // 2. Delete the Refresh Token (so they can't get a NEW token)
        userRepository.findByUsername(username).ifPresent(refreshTokenRepository::deleteByUser);
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }
}