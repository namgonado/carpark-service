package com.nam.provider.carpark.availability.cache;

import com.nam.model.CarPark;
import com.nam.repository.CarParkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryMapSyncUpCache implements CarParkSyncUpCache {
    public static final int INITIAL_PAGE = 0;
    public static final int PAGE_SIZE = 1000;
    private final Map<String, LocalDateTime> cache = new ConcurrentHashMap<>();

    @Autowired
    private CarParkRepository carParkRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        loadCarParksIntoCache();
    }

    private synchronized void loadCarParksIntoCache() {
        int page = INITIAL_PAGE;
        Page<CarPark> carParkPage;

        do {
            /**
             * For large databases with millions of records, define a custom query to load only the required fields:
             * - carParkNumber
             * - updateDateTime
             * This reduces the amount of data retrieved during bulk queries.
             */
            carParkPage = carParkRepository.findAll(PageRequest.of(page, PAGE_SIZE));
            carParkPage
                    .forEach(carPark -> cache.put(carPark.getCarparkNumber(), carPark.getUpdateDatetime()));
            page++;
        } while (carParkPage.hasNext());
    }

    @Override
    public String getCacheType() {
        return "IN_MEMORY";
    }

    @Override
    public boolean contains(String carparkNumber) {
        return cache.containsKey(carparkNumber);
    }

    @Override
    public LocalDateTime getUpdateTime(String carparkNumber) {
        return cache.get(carparkNumber);
    }

    @Override
    public void putUpdateTime(String carparkNumber, LocalDateTime updateTime) {
        cache.put(carparkNumber, updateTime);
    }
}