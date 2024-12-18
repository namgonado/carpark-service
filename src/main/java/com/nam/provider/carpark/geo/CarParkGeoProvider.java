package com.nam.provider.carpark.geo;

import com.nam.model.CarParkGeo;

import java.util.List;

public interface CarParkGeoProvider {
    CarParkGeo getCarParkByNo(String carParkNo);
    List<CarParkGeo> getAllCarPark();
}
