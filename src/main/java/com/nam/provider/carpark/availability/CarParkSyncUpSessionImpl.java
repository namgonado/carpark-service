package com.nam.provider.carpark.availability;

import com.nam.model.CarPark;
import com.nam.provider.carpark.availability.cache.CarParkSyncUpCache;
import com.nam.provider.carpark.availability.cache.factory.SyncUpCacheFactory;
import com.nam.repository.CarParkRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CarParkSyncUpSessionImpl implements CarParkSyncUpSession {
    public static final String CACHE_TIMESTAMP = "CACHE_TIMESTAMP";
    @Autowired
    private CarParkRepository carParkRepository;

    @Autowired
    private SyncUpCacheFactory syncUpCacheFactory;

    private CarParkSyncUpCache cache;

    @PostConstruct
    public void init() {
        cache = syncUpCacheFactory.getCache();
    }

    @Override
    public boolean exists(String carparkNumber) {
        return cache.contains(carparkNumber);
    }

    @Override
    public boolean shouldUpdate(String carparkNumber, LocalDateTime updateDatetime) {
        LocalDateTime cachedDatetime = cache.getUpdateTime(carparkNumber);
        return cachedDatetime == null || !cachedDatetime.equals(updateDatetime);
    }

    @Override
    public void updateCache(String carparkNumber, LocalDateTime updateDatetime) {
        cache.putUpdateTime(carparkNumber, updateDatetime);
    }

    @Override
    public Optional<CarPark> getCarPark(String carparkNumber) {
        return carParkRepository.findById(carparkNumber);
    }

    @Override
    public void saveCarPark(CarPark carPark) {
        carParkRepository.save(carPark);
    }

    @Override
    public boolean shouldSyncUp(LocalDateTime itemTimestamp) {
        LocalDateTime cacheTimestamp = cache.getUpdateTime(CACHE_TIMESTAMP);
        return cacheTimestamp == null || !cacheTimestamp.equals(itemTimestamp);
    }

    @Override
    public void updateTimestamp(LocalDateTime itemTimestamp) {
        cache.putUpdateTime(CACHE_TIMESTAMP, itemTimestamp);
    }
}
