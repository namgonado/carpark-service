package com.nam.provider;

import com.nam.model.CarParkGeo;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nam.provider.constant.CarParkGeoHeaders.*;

@Component
public class CarParkGeoProviderImpl implements CarParkGeoProvider {

    private static final String CSV_FILE_PATH = "carParkData/HDBCarparkInformation.csv";
    @Autowired
    ApplicationContext applicationContext;
    private Map<String, CarParkGeo> carParkGeoMap = new HashMap<>();

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        Resource resource = applicationContext.getResource("classpath:" + CSV_FILE_PATH);
        try (Reader reader = new InputStreamReader(resource.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                CarParkGeo carParkGeo = new CarParkGeo();
                carParkGeo.setCarParkNo(csvRecord.get(CAR_PARK_NO.getHeader()));
                carParkGeo.setAddress(csvRecord.get(ADDRESS.getHeader()));
                carParkGeo.setXCoord(Double.parseDouble(csvRecord.get(X_COORD.getHeader())));
                carParkGeo.setYCoord(Double.parseDouble(csvRecord.get(Y_COORD.getHeader())));
                carParkGeo.setCarParkType(csvRecord.get(CAR_PARK_TYPE.getHeader()));
                carParkGeo.setTypeOfParkingSystem(csvRecord.get(TYPE_OF_PARKING_SYSTEM.getHeader()));
                carParkGeo.setShortTermParking(csvRecord.get(SHORT_TERM_PARKING.getHeader()));
                carParkGeo.setFreeParking(csvRecord.get(FREE_PARKING.getHeader()));
                carParkGeo.setNightParking("YES".equalsIgnoreCase(csvRecord.get(NIGHT_PARKING.getHeader())));
                carParkGeo.setCarParkDecks(Integer.parseInt(csvRecord.get(CAR_PARK_DECKS.getHeader())));
                carParkGeo.setGantryHeight(Double.parseDouble(csvRecord.get(GANTRY_HEIGHT.getHeader())));
                carParkGeo.setCarParkBasement("Y".equalsIgnoreCase(csvRecord.get(CAR_PARK_BASEMENT.getHeader())));

                carParkGeoMap.put(carParkGeo.getCarParkNo(), carParkGeo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CarParkGeo getCarParkByNo(String carParkNo) {
        return carParkGeoMap.get(carParkNo);
    }

    @Override
    public List<CarParkGeo> getAllCarPark() {
        return new ArrayList<>(carParkGeoMap.values());
    }
}