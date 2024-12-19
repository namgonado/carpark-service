package com.nam.carpark.provider.availability;

import com.nam.carpark.model.CarPark;
import com.nam.carpark.model.dto.CarParkAvailabilityResponse;
import com.nam.carpark.utils.DateTimeUtils;
import com.nam.carpark.utils.EntityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CarParkAvailabilityProviderImpl implements CarParkAvailabilityProvider {
    private static final Logger logger = LoggerFactory.getLogger(CarParkAvailabilityProviderImpl.class);

    @Value("${carpark.provider.availability.external-source}")
    private String externalUrl;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CarParkSyncUpSession carParkSyncUpSession;

    @Override
    public synchronized void poll() {
        CarParkAvailabilityResponse response = restTemplate.getForObject(externalUrl, CarParkAvailabilityResponse.class);
        boolean responseNotEmpty = response != null && response.getItems() != null && !response.getItems().isEmpty();

        if (responseNotEmpty) {
            CarParkAvailabilityResponse.Item mainItemResponse = response.getItems().get(0);
            LocalDateTime currentTimestamp = DateTimeUtils.parseDateTimeWithZone(mainItemResponse.getTimestamp());

            if (carParkSyncUpSession.shouldSyncUp(currentTimestamp)) {
                logger.info("Timestamp has changed to {}, start to sync up with local database", currentTimestamp);

                int updatedCount = 0;
                int addedCount = 0;

                for (CarParkAvailabilityResponse.CarParkData data : mainItemResponse.getCarparkData()) {
                    LocalDateTime updateDatetime = LocalDateTime.parse(data.getUpdateDatetime());
                    String carparkNumber = data.getCarparkNumber();

                    if (carParkSyncUpSession.exists(carparkNumber) && carParkSyncUpSession.shouldUpdate(carparkNumber, updateDatetime)) {
                        updateChangedCarPark(carparkNumber, data);
                        updatedCount++;
                    } else if (!carParkSyncUpSession.exists(carparkNumber)) {
                        addNewCarPark(data);
                        addedCount++;
                    }

                    carParkSyncUpSession.updateCache(carparkNumber, updateDatetime);
                }
                carParkSyncUpSession.updateTimestamp(currentTimestamp);
                logger.info("Total number of car park updated: {}", updatedCount);
                logger.info("Total number of car park added: {}", addedCount);
            }
        }
    }

    private void updateChangedCarPark(String carparkNumber, CarParkAvailabilityResponse.CarParkData data) {
        Optional<CarPark> optionalCarPark = carParkSyncUpSession.getCarPark(carparkNumber);
        if (optionalCarPark.isPresent()) {
            CarPark carPark = optionalCarPark.get();
            EntityHelper.populateCarParkInfo(carPark, data);
            carParkSyncUpSession.saveCarPark(carPark);
        }else {
            throw new IllegalStateException("Car park with number " + carparkNumber + " does not exist for update.");
        }
    }

    private void addNewCarPark(CarParkAvailabilityResponse.CarParkData data) {
        CarPark carPark = EntityHelper.createNewCarPark(data);
        carParkSyncUpSession.saveCarPark(carPark);
    }
}