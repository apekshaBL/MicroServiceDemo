package auth_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SessionManagementService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // This handles the "1 Device Limit" logic
    public void registerNewSession(String username, String tenantId, String newAccessToken) {
        String sessionKey = "SESSION:" + tenantId + ":" + username;

        // 1. Check if user is already logged in somewhere else
        String oldToken = redisTemplate.opsForValue().get(sessionKey);

        if (oldToken != null) {
            // 2. Kick out the old device by adding its token to the Blacklist!
            redisTemplate.opsForValue().set("BLACKLIST:" + oldToken, "true", 30, TimeUnit.MINUTES);
            System.out.println(" Kicked out old session for user: " + username);
        }

        // 3. Register the NEW device session
        redisTemplate.opsForValue().set(sessionKey, newAccessToken, 30, TimeUnit.MINUTES);
    }
}

