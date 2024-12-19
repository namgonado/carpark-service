package com.nam.provider.carpark.availability.cache.factory;

import com.nam.provider.carpark.availability.cache.CarParkSyncUpCache;

public interface SyncUpCacheFactory {
    CarParkSyncUpCache getCache();
}
