package com.nam.carpark.provider.availability.cache.factory;

import com.nam.carpark.provider.availability.cache.CarParkSyncUpCache;

public interface SyncUpCacheFactory {
    CarParkSyncUpCache getCache();
}
