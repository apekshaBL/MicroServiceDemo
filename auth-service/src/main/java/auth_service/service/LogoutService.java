package auth_service.service;

import auth_service.repository.RefreshTokenRepository;
import auth_service.repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LogoutService {

    @Autowired
    private StringRedisTemplate redisTemplate; // <--- This is your new Redis connection!

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserCredentialRepository userRepository;

    @Transactional
    public void logout(String accessToken, String username) {

        redisTemplate.opsForValue().set("BLACKLIST:" + accessToken, "true", 30, TimeUnit.MINUTES);


        userRepository.findByUsername(username).ifPresent(refreshTokenRepository::deleteByUser);
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("BLACKLIST:" + token));
    }
}