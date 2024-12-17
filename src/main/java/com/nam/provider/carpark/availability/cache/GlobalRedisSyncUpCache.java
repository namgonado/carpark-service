package com.nam.provider.carpark.availability.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GlobalRedisSyncUpCache implements CarParkSyncUpCache {

    @Autowired
    private RedisTemplate<String, LocalDateTime> redisTemplate;

    @Override
    public String getCacheType() {
        return "GLOBAL_REDIS";
    }

    @Override
    public boolean contains(String carparkNumber) {
        return redisTemplate.hasKey(carparkNumber);
    }

    @Override
    public LocalDateTime getUpdateTime(String carparkNumber) {
        return redisTemplate.opsForValue().get(carparkNumber);
    }

    @Override
    public void putUpdateTime(String carparkNumber, LocalDateTime updateTime) {
        redisTemplate.opsForValue().set(carparkNumber, updateTime);
    }
}