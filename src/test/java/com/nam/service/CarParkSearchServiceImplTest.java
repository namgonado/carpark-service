package com.nam.service;

import com.nam.model.CarPark;
import com.nam.model.CarParkInfo;
import com.nam.model.dto.SearchCarParkResponse;
import com.nam.provider.carpark.geo.CarParkGeoProviderImpl;
import com.nam.repository.CarParkRepository;
import com.nam.utils.CoordinateConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class CarParkSearchServiceImplTest {

    @InjectMocks
    private CarParkSearchServiceImpl carParkSearchService = new CarParkSearchServiceImpl();

    @Mock
    private CarParkRepository carParkRepository;

    @Mock
    private ApplicationContext applicationContext;

    private CarParkGeoProviderImpl carParkGeoProvider;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        //Load car park data from csv
        carParkGeoProvider = new CarParkGeoProviderImpl();
        Resource resource = new ClassPathResource("carParkData/HDBCarparkInformation.csv");
        when(applicationContext.getResource("classpath:carParkData/HDBCarparkInformation.csv")).thenReturn(resource);
        ReflectionTestUtils.setField(carParkGeoProvider, "applicationContext", applicationContext);
        carParkGeoProvider.init();

        ReflectionTestUtils.setField(carParkSearchService, "carParkGeoProvider", carParkGeoProvider);

        // Mock CarPark data in repository
        CarPark carPark1 = new CarPark();
        carPark1.setCarparkNumber("AR2M");
        carPark1.setCarParkInfos(Arrays.asList(new CarParkInfo(1L, "A", 100, 50, carPark1)));

        CarPark carPark2 = new CarPark();
        carPark2.setCarparkNumber("A24");
        carPark2.setCarParkInfos(Arrays.asList(new CarParkInfo(10L, "C", 200, 150, carPark2)));

        when(carParkRepository.findById("AR2M")).thenReturn(Optional.of(carPark1));
        when(carParkRepository.findById("A24")).thenReturn(Optional.of(carPark2));
    }

    @Test
    public void testFindNearestCarParks() {
        double latitude = 1.37326;
        double longitude = 103.897;
        int page = 0;
        int perPage = 10;

        List<SearchCarParkResponse> carParks = carParkSearchService.findNearestCarParks(latitude, longitude, page, perPage);

        assertNotNull(carParks);
        assertEquals(2, carParks.size());

        // Verify the order by distance
        double distance1 = CoordinateConverter.distance(latitude, longitude, carParks.get(0).getLatitude(), carParks.get(0).getLongitude());
        double distance2 = CoordinateConverter.distance(latitude, longitude, carParks.get(1).getLatitude(), carParks.get(1).getLongitude());

        assertEquals(true, distance1 <= distance2);
    }
}
