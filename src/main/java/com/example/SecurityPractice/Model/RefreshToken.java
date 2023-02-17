package com.example.SecurityPractice.Model;

import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@Getter
@RedisHash(timeToLive = 86400 * 14)
public class RefreshToken {

    @Id
    private String id;

    public RefreshToken(String id) {
        this.id = id;
    }
}
