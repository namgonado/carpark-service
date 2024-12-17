package com.nam.provider;

import com.nam.model.CarParkGeo;

import java.util.List;

interface CarParkGeoProvider {
    CarParkGeo getCarParkByNo(String carParkNo);
    List<CarParkGeo> getAllCarPark();
}
