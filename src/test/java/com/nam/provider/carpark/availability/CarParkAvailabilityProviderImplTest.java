package com.nam.provider.carpark.availability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nam.model.CarPark;
import com.nam.model.dto.CarParkAvailabilityResponse;
import com.nam.utils.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CarParkAvailabilityProviderImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CarParkSyncUpSession carParkSyncUpSession;

    @InjectMocks
    private CarParkAvailabilityProviderImpl carParkProvider;

    private CarParkAvailabilityResponse response;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        response = objectMapper.readValue(new File("src/test/resources/carpark/availability.json"), CarParkAvailabilityResponse.class);
        ReflectionTestUtils.setField(carParkProvider, "externalUrl", "https://api.data.gov.sg/v1/transport/carpark-availability");
    }

    @Test
    void testPollWithUpdatedTimestamp() {
        CarParkAvailabilityResponse.Item item = response.getItems().get(0);
        item.setTimestamp(DateTimeUtils.nowAsDateTimeWithZoneString());

        //GIVEN
        when(restTemplate.getForObject(anyString(), eq(CarParkAvailabilityResponse.class))).thenReturn(response);
        when(carParkSyncUpSession.shouldSyncUp(any(LocalDateTime.class))).thenReturn(true);
        when(carParkSyncUpSession.shouldUpdate(anyString(), any(LocalDateTime.class))).thenReturn(true);
        when(carParkSyncUpSession.getCarPark(anyString())).thenReturn(Optional.of(new CarPark()));
        when(carParkSyncUpSession.exists(anyString())).thenAnswer(invocation -> {
            String carParkNumber = invocation.getArgument(0);
            return "BM29".equals(carParkNumber);
        });

        //WHEN
        carParkProvider.poll();

        //THEN
        verify(carParkSyncUpSession, times(13)).saveCarPark(any(CarPark.class));
        verify(carParkSyncUpSession, times(1)).updateTimestamp(any(LocalDateTime.class));
    }

    @Test
    void testPollWithNewCarPark() {
        // Create a list to store the captured arguments
        List<CarPark> savedCarParks = new ArrayList<>();

        //GIVEN
        CarParkAvailabilityResponse.Item item = response.getItems().get(0);
        item.setTimestamp(DateTimeUtils.nowAsDateTimeWithZoneString());

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
        // Use doAnswer to capture the argument and add it to the list
        doAnswer(invocation -> {
            CarPark carPark = invocation.getArgument(0);
            savedCarParks.add(carPark);
            return null;
        }).when(carParkSyncUpSession).saveCarPark(any(CarPark.class));

        //WHEN
        carParkProvider.poll();

        //THEN
        ArgumentCaptor<CarPark> carParkCaptor = ArgumentCaptor.forClass(CarPark.class);
        verify(carParkSyncUpSession, times(1)).saveCarPark(any(CarPark.class));
        verify(carParkSyncUpSession, times(1)).updateTimestamp(any(LocalDateTime.class));

        assertEquals(1, savedCarParks.size());
        CarPark savedCarPark = savedCarParks.get(0);
        assertEquals("BM29", savedCarPark.getCarparkNumber());
        assertEquals("2024-12-17T12:39:11", savedCarPark.getUpdateDatetime().toString());
        assertEquals(1, savedCarPark.getCarParkInfos().size());
        assertEquals(97, savedCarPark.getCarParkInfos().get(0).getTotalLots());
        assertEquals("C", savedCarPark.getCarParkInfos().get(0).getLotType());
        assertEquals(0, savedCarPark.getCarParkInfos().get(0).getLotsAvailable());
    }

    @Test
    void testPollWithUnchangedTimestamp() {
        //GIVEN
        CarParkAvailabilityResponse.Item item = response.getItems().get(0);
        item.setTimestamp(DateTimeUtils.nowAsDateTimeWithZoneString());

        when(restTemplate.getForObject(anyString(), eq(CarParkAvailabilityResponse.class))).thenReturn(response);
        when(carParkSyncUpSession.shouldSyncUp(any(LocalDateTime.class))).thenReturn(false);

        //WHEN
        carParkProvider.poll();

        //THEN
        verify(carParkSyncUpSession, never()).updateCache(anyString(), any(LocalDateTime.class));
        verify(carParkSyncUpSession, never()).updateTimestamp(any(LocalDateTime.class));
    }
}
