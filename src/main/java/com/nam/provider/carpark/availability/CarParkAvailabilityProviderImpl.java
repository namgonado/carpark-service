package com.nam.provider.carpark.availability;

import com.nam.model.CarPark;
import com.nam.model.dto.CarParkAvailabilityResponse;
import com.nam.utils.DateTimeUtils;
import com.nam.utils.EntityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CarParkAvailabilityProviderImpl implements CarParkAvailabilityProvider {

    private static final String API_URL = "https://api.data.gov.sg/v1/transport/carpark-availability";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CarParkSyncUpSession carParkSyncUpSession;

    @Override
    public synchronized void poll() {
        CarParkAvailabilityResponse response = restTemplate.getForObject(API_URL, CarParkAvailabilityResponse.class);
        boolean responseNotEmpty = response != null && response.getItems() != null && !response.getItems().isEmpty();

        if (responseNotEmpty) {
            CarParkAvailabilityResponse.Item mainItemResponse = response.getItems().get(0);
            LocalDateTime currentTimestamp = DateTimeUtils.parseDateTimeWithZone(mainItemResponse.getTimestamp());

            if (carParkSyncUpSession.shouldSyncUp(currentTimestamp)) {
                for (CarParkAvailabilityResponse.CarParkData data : mainItemResponse.getCarparkData()) {
                    LocalDateTime updateDatetime = LocalDateTime.parse(data.getUpdateDatetime());
                    String carparkNumber = data.getCarparkNumber();

                    if (carParkSyncUpSession.exists(carparkNumber) && carParkSyncUpSession.shouldUpdate(carparkNumber, updateDatetime)) {
                        updateChangedCarPark(carparkNumber, data);
                    } else if (!carParkSyncUpSession.exists(carparkNumber)) {
                        addNewCarPark(data);
                    }

                    carParkSyncUpSession.updateCache(carparkNumber, updateDatetime);
                }
                carParkSyncUpSession.updateTimestamp(currentTimestamp);
            }
        }
    }

    private void updateChangedCarPark(String carparkNumber, CarParkAvailabilityResponse.CarParkData data) {
        Optional<CarPark> optionalCarPark = carParkSyncUpSession.getCarPark(carparkNumber);
        if (optionalCarPark.isPresent()) {
            CarPark carPark = optionalCarPark.get();
            EntityHelper.populateCarParkInfo(carPark, data);
            carParkSyncUpSession.saveCarPark(carPark);
        }
    }

    private void addNewCarPark(CarParkAvailabilityResponse.CarParkData data) {
        CarPark carPark = EntityHelper.createNewCarPark(data);
        carParkSyncUpSession.saveCarPark(carPark);
    }
}