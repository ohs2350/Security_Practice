package com.example.SecurityPractice.security.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token 을 Redis에 저장
    public void insertRefreshToken(String id, String refresh) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(id, refresh, 60*60*24 *14, TimeUnit.SECONDS);

        System.out.println("Refresh Token 저장 : " + refresh);
    }
}
