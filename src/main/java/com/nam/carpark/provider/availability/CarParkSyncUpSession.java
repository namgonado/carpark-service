package com.nam.carpark.provider.availability;

import com.nam.carpark.model.CarPark;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CarParkSyncUpSession {
    boolean exists(String carparkNumber);

    boolean shouldUpdate(String carparkNumber, LocalDateTime updateDatetime);

    void updateCache(String carparkNumber, LocalDateTime updateDatetime);

    Optional<CarPark> getCarPark(String carparkNumber);

    void saveCarPark(CarPark carPark);

    boolean shouldSyncUp(LocalDateTime itemTimestamp);

    void updateTimestamp(LocalDateTime itemTimestamp);
}
