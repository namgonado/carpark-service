package com.nam.carpark.provider.availability.cache.factory;

import com.nam.carpark.provider.availability.cache.CarParkSyncUpCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SyncUpCacheFactoryImpl implements SyncUpCacheFactory {
    private static final Logger logger = LoggerFactory.getLogger(SyncUpCacheFactoryImpl.class);

    @Value("${carpark.provider.availability.syncup.cache:IN_MEMORY}")
    private String configuredType;

    @Autowired
    private List<CarParkSyncUpCache> syncUpCaches;


    @Override
    public CarParkSyncUpCache getCache() {
        CarParkSyncUpCache enabledCached = syncUpCaches.stream()
                .filter(cache -> cache.getCacheType().equalsIgnoreCase(configuredType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No cache found for type: " + configuredType));
        logger.info("Enabled sync up cache type: {}", configuredType);

        return enabledCached;
    }
}
