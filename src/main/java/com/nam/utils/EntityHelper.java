package com.nam.utils;

import com.nam.model.CarPark;
import com.nam.model.CarParkGeo;
import com.nam.model.CarParkInfo;
import com.nam.model.dto.CarParkAvailabilityResponse;
import com.thirparty.net.qxcg.svy21.LatLonCoordinate;
import org.apache.commons.csv.CSVRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.nam.provider.constant.CarParkGeoHeaders.*;
import static com.nam.provider.constant.CarParkGeoHeaders.CAR_PARK_BASEMENT;

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

    public static CarParkGeo fromCsvToCarParkGeo(CSVRecord csvRecord) {
        CarParkGeo carParkGeo = new CarParkGeo();
        carParkGeo.setCarParkNo(csvRecord.get(CAR_PARK_NO.getHeader()));
        carParkGeo.setAddress(csvRecord.get(ADDRESS.getHeader()));
        carParkGeo.setCarParkType(csvRecord.get(CAR_PARK_TYPE.getHeader()));
        carParkGeo.setTypeOfParkingSystem(csvRecord.get(TYPE_OF_PARKING_SYSTEM.getHeader()));
        carParkGeo.setShortTermParking(csvRecord.get(SHORT_TERM_PARKING.getHeader()));
        carParkGeo.setFreeParking(csvRecord.get(FREE_PARKING.getHeader()));
        carParkGeo.setNightParking("YES".equalsIgnoreCase(csvRecord.get(NIGHT_PARKING.getHeader())));
        carParkGeo.setCarParkDecks(Integer.parseInt(csvRecord.get(CAR_PARK_DECKS.getHeader())));
        carParkGeo.setGantryHeight(Double.parseDouble(csvRecord.get(GANTRY_HEIGHT.getHeader())));
        carParkGeo.setCarParkBasement("Y".equalsIgnoreCase(csvRecord.get(CAR_PARK_BASEMENT.getHeader())));

        //Convert svy21 coordinate to lat long
        double east = Double.parseDouble(csvRecord.get(X_COORD.getHeader()));
        double north = Double.parseDouble(csvRecord.get(Y_COORD.getHeader()));
        LatLonCoordinate latLonCoordinate = CoordinateConverter.toLatLong(north, east);
        carParkGeo.setLat(latLonCoordinate.getLatitude());
        carParkGeo.setLon(latLonCoordinate.getLongitude());

        return carParkGeo;
    }
}