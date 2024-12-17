package com.nam.provider.carpark.availability.cache;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nam.model.CarPark;
import com.nam.model.dto.CarParkAvailabilityResponse;
import com.nam.repository.CarParkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryMapSyncUpCacheTest {

    @InjectMocks
    private InMemoryMapSyncUpCache inMemoryMapSyncUpCache;

    @Mock
    private CarParkRepository carParkRepository;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        ObjectMapper objectMapper = new ObjectMapper();
        CarParkAvailabilityResponse response = objectMapper.readValue(new File("src/test/resources/carpark/availability.json"), CarParkAvailabilityResponse.class);

        List<CarPark> carParks = response.getItems().get(0).getCarparkData().stream().map(data -> {
            CarPark carPark = new CarPark();
            carPark.setCarparkNumber(data.getCarparkNumber());
            carPark.setUpdateDatetime(LocalDateTime.parse(data.getUpdateDatetime()));
            return carPark;
        }).toList();

        Page<CarPark> carParkPage = new PageImpl<>(carParks);
        when(carParkRepository.findAll(PageRequest.of(0, 100))).thenReturn(carParkPage);
        when(carParkRepository.findAll(PageRequest.of(1, 100))).thenReturn(Page.empty());

        inMemoryMapSyncUpCache.init();
    }

    @Test
    void testCacheInitialization() {
        assertTrue(inMemoryMapSyncUpCache.contains("BM29"));
        assertNotNull(inMemoryMapSyncUpCache.getUpdateTime("BM29"));
    }

    @Test
    void testPutUpdateTime() {
        LocalDateTime newUpdateTime = LocalDateTime.now();
        inMemoryMapSyncUpCache.putUpdateTime("BM29", newUpdateTime);
        assertEquals(newUpdateTime, inMemoryMapSyncUpCache.getUpdateTime("BM29"));
    }

    @Test
    void testContains() {
        assertTrue(inMemoryMapSyncUpCache.contains("BM29"));
        assertFalse(inMemoryMapSyncUpCache.contains("BM30"));
    }

    @Test
    void testGetUpdateTime() {
        LocalDateTime updateTime = inMemoryMapSyncUpCache.getUpdateTime("BM29");
        assertNotNull(updateTime);
    }
}