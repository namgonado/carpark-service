package com.nam.provider.carpark.availability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nam.model.CarPark;
import com.nam.model.dto.CarParkAvailabilityResponse;
import com.nam.utils.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CarParkProviderImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CarParkSyncUpSession carParkSyncUpSession;

    @InjectMocks
    private CarParkProviderImpl carParkProvider;

    private CarParkAvailabilityResponse response;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        response = objectMapper.readValue(new File("src/test/resources/carpark/availability.json"), CarParkAvailabilityResponse.class);
    }

    @Test
    void testPollWithUpdatedTimestamp() {
        CarParkAvailabilityResponse.Item item = response.getItems().get(0);
        item.setTimestamp(DateTimeUtils.nowAsString());

        when(restTemplate.getForObject(anyString(), eq(CarParkAvailabilityResponse.class))).thenReturn(response);
        when(carParkSyncUpSession.shouldSyncUp(any(LocalDateTime.class))).thenReturn(true);
        when(carParkSyncUpSession.shouldUpdate(anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(carParkSyncUpSession.getCarPark(anyString())).thenReturn(Optional.of(new CarPark()));
        when(carParkSyncUpSession.exists(anyString())).thenAnswer(invocation -> {
            String carParkNumber = invocation.getArgument(0);
            return "BM29".equals(carParkNumber);
        });

        carParkProvider.poll();

        verify(carParkSyncUpSession, times(13)).saveCarPark(any(CarPark.class));
        verify(carParkSyncUpSession, times(1)).updateTimestamp(any(LocalDateTime.class));
    }

    @Test
    void testPollWithNewCarPark() {
        CarParkAvailabilityResponse.Item item = response.getItems().get(0);
        item.setTimestamp(DateTimeUtils.nowAsString());

        when(restTemplate.getForObject(anyString(), eq(CarParkAvailabilityResponse.class))).thenReturn(response);
        when(carParkSyncUpSession.shouldSyncUp(any(LocalDateTime.class))).thenReturn(true);
        when(carParkSyncUpSession.exists(anyString())).thenAnswer(invocation -> {
            String carParkNumber = invocation.getArgument(0);
            return !"BM29".equals(carParkNumber);
        });
        when(carParkSyncUpSession.shouldUpdate(anyString(), any(LocalDateTime.class))).thenAnswer(invocation -> {
            String carParkNumber = invocation.getArgument(0);
            return "BM29".equals(carParkNumber);
        });

        carParkProvider.poll();

        verify(carParkSyncUpSession, times(1)).saveCarPark(any(CarPark.class));
        verify(carParkSyncUpSession, times(1)).updateTimestamp(any(LocalDateTime.class));
    }

    @Test
    void testPollWithUnchangedTimestamp() {
        CarParkAvailabilityResponse.Item item = response.getItems().get(0);
        item.setTimestamp(DateTimeUtils.nowAsString());

        when(restTemplate.getForObject(anyString(), eq(CarParkAvailabilityResponse.class))).thenReturn(response);
        when(carParkSyncUpSession.shouldSyncUp(any(LocalDateTime.class))).thenReturn(false);

        carParkProvider.poll();

        verify(carParkSyncUpSession, never()).updateCache(anyString(), any(LocalDateTime.class));
        verify(carParkSyncUpSession, never()).updateTimestamp(any(LocalDateTime.class));
    }
}
