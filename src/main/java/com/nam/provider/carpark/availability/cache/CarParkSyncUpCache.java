package com.nam.provider.carpark.availability.cache;

import java.time.LocalDateTime;

public interface CarParkSyncUpCache {
    String getCacheType();
    boolean contains(String carparkNumber);

    LocalDateTime getUpdateTime(String carparkNumber);

    void putUpdateTime(String carparkNumber, LocalDateTime updateTime);
}
