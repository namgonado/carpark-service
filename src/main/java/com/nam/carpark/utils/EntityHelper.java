package com.nam.carpark.utils;

import com.nam.carpark.model.CarPark;
import com.nam.carpark.model.CarParkGeo;
import com.nam.carpark.model.CarParkInfo;
import com.nam.carpark.model.dto.CarParkAvailabilityResponse;
import com.nam.carpark.provider.geo.constant.CarParkGeoHeaders;
import com.thirparty.net.qxcg.svy21.LatLonCoordinate;
import org.apache.commons.csv.CSVRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityHelper {

    public static void populateCarParkInfo(CarPark carPark, CarParkAvailabilityResponse.CarParkData data) {
        carPark.setUpdateDatetime(LocalDateTime.parse(data.getUpdateDatetime()));
        List<CarParkInfo> existingInfos = carPark.getCarParkInfos();
        Map<String, CarParkInfo> existingInfoMap = existingInfos.stream()
                .collect(Collectors.toMap(CarParkInfo::getLotType, info -> info));

        for (CarParkAvailabilityResponse.CarParkInfo newInfo : data.getCarparkInfo()) {
            CarParkInfo existingInfo = existingInfoMap.get(newInfo.getLotType());
            if (existingInfo != null) {
                existingInfo.setTotalLots(Integer.parseInt(newInfo.getTotalLots()));
                existingInfo.setLotsAvailable(Integer.parseInt(newInfo.getLotsAvailable()));
            } else {
                CarParkInfo carParkInfo = new CarParkInfo();
                carParkInfo.setLotType(newInfo.getLotType());
                carParkInfo.setTotalLots(Integer.parseInt(newInfo.getTotalLots()));
                carParkInfo.setLotsAvailable(Integer.parseInt(newInfo.getLotsAvailable()));
                carParkInfo.setCarPark(carPark);
                existingInfos.add(carParkInfo);
            }
        }
        carPark.setCarParkInfos(existingInfos);
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

    public static CarParkGeo fromCsvToCarParkGeo(CSVRecord csvRecord) {
        CarParkGeo carParkGeo = new CarParkGeo();
        carParkGeo.setCarParkNo(csvRecord.get(CarParkGeoHeaders.CAR_PARK_NO.getHeader()));
        carParkGeo.setAddress(csvRecord.get(CarParkGeoHeaders.ADDRESS.getHeader()));
        carParkGeo.setCarParkType(csvRecord.get(CarParkGeoHeaders.CAR_PARK_TYPE.getHeader()));
        carParkGeo.setTypeOfParkingSystem(csvRecord.get(CarParkGeoHeaders.TYPE_OF_PARKING_SYSTEM.getHeader()));
        carParkGeo.setShortTermParking(csvRecord.get(CarParkGeoHeaders.SHORT_TERM_PARKING.getHeader()));
        carParkGeo.setFreeParking(csvRecord.get(CarParkGeoHeaders.FREE_PARKING.getHeader()));
        carParkGeo.setNightParking("YES".equalsIgnoreCase(csvRecord.get(CarParkGeoHeaders.NIGHT_PARKING.getHeader())));
        carParkGeo.setCarParkDecks(Integer.parseInt(csvRecord.get(CarParkGeoHeaders.CAR_PARK_DECKS.getHeader())));
        carParkGeo.setGantryHeight(Double.parseDouble(csvRecord.get(CarParkGeoHeaders.GANTRY_HEIGHT.getHeader())));
        carParkGeo.setCarParkBasement("Y".equalsIgnoreCase(csvRecord.get(CarParkGeoHeaders.CAR_PARK_BASEMENT.getHeader())));

        //Convert svy21 coordinate to lat long
        double east = Double.parseDouble(csvRecord.get(CarParkGeoHeaders.X_COORD.getHeader()));
        double north = Double.parseDouble(csvRecord.get(CarParkGeoHeaders.Y_COORD.getHeader()));
        LatLonCoordinate latLonCoordinate = CoordinateConverter.toLatLong(north, east);
        carParkGeo.setLat(latLonCoordinate.getLatitude());
        carParkGeo.setLon(latLonCoordinate.getLongitude());

        return carParkGeo;
    }
}