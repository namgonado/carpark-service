package com.nam.carpark.provider.availability;

import com.nam.carpark.exception.CarParkPollingException;
import com.nam.carpark.model.CarPark;
import com.nam.carpark.model.dto.CarParkAvailabilityResponse;
import com.nam.carpark.model.dto.CarParkAvailabilityResponse.CarParkData;
import com.nam.carpark.repository.CarParkRepository;
import com.nam.carpark.utils.DateTimeUtils;
import com.nam.carpark.utils.EntityHelper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CarParkAvailabilityProviderImpl implements CarParkAvailabilityProvider {
    private static final Logger logger = LoggerFactory.getLogger(CarParkAvailabilityProviderImpl.class);

    private static final String UPDATE_LIST = "UPDATE_LIST";
    private static final String NEW_LIST = "NEW_LIST";

    private static final int BATCH_SIZE = 500;
    @Autowired
    CarParkRepository carParkRepository;
    @Value("${carpark.provider.availability.external-source}")
    private String externalUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CarParkSyncUpSession carParkSyncUpSession;

    @Override
    @Transactional
    public synchronized void poll() {
        try {
            CarParkAvailabilityResponse response = requestExternalData();
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                CarParkAvailabilityResponse.Item mainItemResponse = response.getItems().get(0);
                LocalDateTime currentTimestamp = DateTimeUtils.parseDateTimeWithZone(mainItemResponse.getTimestamp());

                if (carParkSyncUpSession.shouldSyncUp(currentTimestamp)) {
                    logger.info("Timestamp has changed to {}, start to sync up with local database", currentTimestamp);
                    processInBatch(mainItemResponse.getCarparkData());
                    carParkSyncUpSession.updateTimestamp(currentTimestamp);
                } else {
                    logger.info("Timestamp {} has not changed, no sync up will happen", currentTimestamp);
                }
            }
        } catch (Exception exception) {
            logger.error("Polling to URL {} failed due to error: {}", externalUrl, exception.getMessage(), exception);
            throw new CarParkPollingException("Failed to poll car park availability due to an error", exception);
        }
    }

    private CarParkAvailabilityResponse requestExternalData() {
        return restTemplate.getForObject(externalUrl, CarParkAvailabilityResponse.class);
    }

    private void processInBatch(List<CarParkData> carparkDataList) {
        int updatedCount = 0;
        int addedCount = 0;

        List<CarParkData> sortedCarparkDataList = carparkDataList.stream()
                .sorted(Comparator.comparing(data -> LocalDateTime.parse(data.getUpdateDatetime())))
                .collect(Collectors.toList());

        for (int i = 0; i < sortedCarparkDataList.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, sortedCarparkDataList.size());
            List<CarParkData> dataSublist = sortedCarparkDataList.subList(i, end);

            Map<String, List<CarParkData>> filterOutLists = filterOutModifyListsAndUpdateCache(dataSublist);

            List<CarParkData> toUpdate = filterOutLists.get(UPDATE_LIST);
            List<CarParkData> toAdd = filterOutLists.get(NEW_LIST);

            updateChangedCarParkInBulk(toUpdate);
            addNewCarParkInBulk(toAdd);

            updatedCount += toUpdate.size();
            addedCount += toAdd.size();
        }

        logger.info("Total number of car park updated: {}", updatedCount);
        logger.info("Total number of car park added: {}", addedCount);
    }

    private Map<String, List<CarParkData>> filterOutModifyListsAndUpdateCache(List<CarParkData> carParkSublist) {
        Map<String, List<CarParkData>> result = new HashMap<>();
        List<CarParkData> toUpdate = new ArrayList<>();
        List<CarParkData> toAdd = new ArrayList<>();

        for (CarParkData data : carParkSublist) {
            LocalDateTime updateDatetime = LocalDateTime.parse(data.getUpdateDatetime());
            String carparkNumber = data.getCarparkNumber();

            if (carParkSyncUpSession.exists(carparkNumber) && carParkSyncUpSession.shouldUpdate(carparkNumber, updateDatetime)) {
                toUpdate.add(data);
                carParkSyncUpSession.updateCache(carparkNumber, updateDatetime);
            } else if (!carParkSyncUpSession.exists(carparkNumber)) {
                toAdd.add(data);
                carParkSyncUpSession.updateCache(carparkNumber, updateDatetime);
            }
        }

        result.put(UPDATE_LIST, toUpdate);
        result.put(NEW_LIST, toAdd);
        return result;
    }

    private void updateChangedCarParkInBulk(List<CarParkData> toUpdate) {
        if (!toUpdate.isEmpty()) {
            List<String> carparkNumbersToUpdate = toUpdate.stream()
                    .map(CarParkData::getCarparkNumber)
                    .collect(Collectors.toList());

            List<CarPark> carParksToUpdate = carParkRepository.findAllById(carparkNumbersToUpdate);
            Map<String, CarPark> carParkMap = carParksToUpdate.stream()
                    .collect(Collectors.toMap(CarPark::getCarparkNumber, carPark -> carPark));

            for (CarParkData data : toUpdate) {
                CarPark carPark = carParkMap.get(data.getCarparkNumber());
                if (carPark != null) {
                    EntityHelper.populateCarParkInfo(carPark, data);
                }
            }

            carParkRepository.saveAll(carParksToUpdate);
        }
    }

    private void addNewCarParkInBulk(List<CarParkData> toAdd) {
        if (!toAdd.isEmpty()) {
            List<CarPark> carParksToAdd = toAdd.stream()
                    .map(EntityHelper::createNewCarPark)
                    .collect(Collectors.toList());
            carParkRepository.saveAll(carParksToAdd);
        }
    }
}