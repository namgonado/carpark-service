package com.nam.provider.carpark.availability.cache.factory;

import com.nam.provider.carpark.availability.cache.CarParkSyncUpCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SyncUpCacheFactoryImpl implements SyncUpCacheFactory {

    @Value("${carpark.provider.availability.syncup.cache:IN_MEMORY}")
    private String configuredType;

    @Autowired
    private List<CarParkSyncUpCache> syncUpCaches;


    @Override
    public CarParkSyncUpCache getCache() {
        return syncUpCaches.stream()
                .filter(cache -> cache.getCacheType().equalsIgnoreCase(configuredType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No cache found for type: " + configuredType));
    }
}
