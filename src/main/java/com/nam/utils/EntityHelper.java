package com.nam.utils;

import com.nam.model.CarPark;
import com.nam.model.CarParkInfo;
import com.nam.model.dto.CarParkAvailabilityResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntityHelper {

    public static void populateCarParkInfo(CarPark carPark, CarParkAvailabilityResponse.CarParkData data) {
        carPark.setUpdateDatetime(LocalDateTime.parse(data.getUpdateDatetime()));
        List<CarParkInfo> carParkInfos = new ArrayList<>();
        for (CarParkAvailabilityResponse.CarParkInfo info : data.getCarparkInfo()) {
            CarParkInfo carParkInfo = new CarParkInfo();
            carParkInfo.setLotType(info.getLotType());
            carParkInfo.setTotalLots(Integer.parseInt(info.getTotalLots()));
            carParkInfo.setLotsAvailable(Integer.parseInt(info.getLotsAvailable()));
            carParkInfo.setCarPark(carPark);
            carParkInfos.add(carParkInfo);
        }
        carPark.setCarParkInfos(carParkInfos);
    }

    public static CarPark createNewCarPark(CarParkAvailabilityResponse.CarParkData data) {
        CarPark carPark = new CarPark();
        carPark.setCarparkNumber(data.getCarparkNumber());
        carPark.setUpdateDatetime(LocalDateTime.parse(data.getUpdateDatetime()));
        List<CarParkInfo> carParkInfos = new ArrayList<>();
        for (CarParkAvailabilityResponse.CarParkInfo info : data.getCarparkInfo()) {
            CarParkInfo carParkInfo = new CarParkInfo();
            carParkInfo.setLotType(info.getLotType());
            carParkInfo.setTotalLots(Integer.parseInt(info.getTotalLots()));
            carParkInfo.setLotsAvailable(Integer.parseInt(info.getLotsAvailable()));
            carParkInfo.setCarPark(carPark);
            carParkInfos.add(carParkInfo);
        }
        carPark.setCarParkInfos(carParkInfos);
        return carPark;
    }
}