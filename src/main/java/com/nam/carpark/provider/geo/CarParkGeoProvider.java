package com.nam.carpark.provider.geo;

import com.nam.carpark.model.CarParkGeo;

import java.util.List;

public interface CarParkGeoProvider {
    CarParkGeo getCarParkByNo(String carParkNo);
    List<CarParkGeo> getAllCarPark();
}
