package com.nam.carpark.provider.availability.cache;

import com.nam.carpark.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class GlobalRedisSyncUpCache implements CarParkSyncUpCache {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
        String updateDateTimeStr = redisTemplate.opsForValue().get(carparkNumber);
        return updateDateTimeStr != null ? LocalDateTime.parse(updateDateTimeStr) : null;
    }

    @Override
    public void putUpdateTime(String carparkNumber, LocalDateTime updateTime) {
        redisTemplate.opsForValue().set(carparkNumber, DateTimeUtils.getDateTimeAsString(updateTime));
    }
}