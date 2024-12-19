package com.nam.carpark.provider.geo;

import com.nam.carpark.model.CarParkGeo;
import com.nam.carpark.utils.EntityHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Component
public class CarParkGeoProviderImpl implements CarParkGeoProvider {
    private static final Logger logger = LoggerFactory.getLogger(CarParkGeoProviderImpl.class);
    private static final String CSV_FILE_PATH = "carParkData/HDBCarparkInformation.csv";
    @Autowired
    ApplicationContext applicationContext;
    private Map<String, CarParkGeo> carParkGeoMap = new HashMap<>();

    @EventListener(ContextRefreshedEvent.class)
    public void init() throws IOException {
        Resource resource = applicationContext.getResource("classpath:" + CSV_FILE_PATH);
        try (Reader reader = new InputStreamReader(resource.getInputStream());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

            for (CSVRecord csvRecord : csvParser) {
                CarParkGeo carParkGeo = EntityHelper.fromCsvToCarParkGeo(csvRecord);
                carParkGeoMap.put(carParkGeo.getCarParkNo(), carParkGeo);
            }
        } catch (IOException e) {
            logger.error("Error parsing CSV file: {}", CSV_FILE_PATH, e);
            throw e;
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