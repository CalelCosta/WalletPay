package recargapay.wallet.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JwtCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void cacheJwt(String username, String jwt) {
        redisTemplate.opsForValue().set(username, jwt, 1, TimeUnit.MINUTES);
    }

    public String getCachedJwt(String username) {
        return redisTemplate.opsForValue().get(username);
    }

    public void invalidateJwt(String username) {
        redisTemplate.delete(username);
    }
}
