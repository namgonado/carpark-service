package com.nam.provider.carpark.availability;

import com.nam.model.CarPark;
import com.nam.provider.carpark.availability.cache.CarParkSyncUpCache;
import com.nam.provider.carpark.availability.cache.SyncUpCacheFactory;
import com.nam.repository.CarParkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CarParkSyncUpSessionImplTest {

    @Mock
    private CarParkRepository carParkRepository;

    @Mock
    private SyncUpCacheFactory syncUpCacheFactory;

    @Mock
    private CarParkSyncUpCache cache;

    @InjectMocks
    private CarParkSyncUpSessionImpl carParkSyncUpSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(syncUpCacheFactory.getCache()).thenReturn(cache);
        carParkSyncUpSession.init();
    }

    @Test
    void testExists() {
        when(cache.contains("BM29")).thenReturn(true);
        assertTrue(carParkSyncUpSession.exists("BM29"));
        verify(cache, times(1)).contains("BM29");
    }

    @Test
    void testShouldUpdate() {
        LocalDateTime now = LocalDateTime.now();
        when(cache.getUpdateTime("BM29")).thenReturn(now.minusDays(1));
        assertTrue(carParkSyncUpSession.shouldUpdate("BM29", now));
        verify(cache, times(1)).getUpdateTime("BM29");
    }

    @Test
    void testUpdateCache() {
        LocalDateTime now = LocalDateTime.now();
        carParkSyncUpSession.updateCache("BM29", now);
        verify(cache, times(1)).putUpdateTime("BM29", now);
    }

    @Test
    void testGetCarPark() {
        CarPark carPark = new CarPark();
        when(carParkRepository.findById("BM29")).thenReturn(Optional.of(carPark));
        assertEquals(Optional.of(carPark), carParkSyncUpSession.getCarPark("BM29"));
        verify(carParkRepository, times(1)).findById("BM29");
    }

    @Test
    void testSaveCarPark() {
        CarPark carPark = new CarPark();
        carParkSyncUpSession.saveCarPark(carPark);
        verify(carParkRepository, times(1)).save(carPark);
    }

    @Test
    void testShouldSyncUp() {
        LocalDateTime now = LocalDateTime.now();
        when(cache.getUpdateTime("CACHE_TIMESTAMP")).thenReturn(now.minusDays(1));
        assertTrue(carParkSyncUpSession.shouldSyncUp(now));
        verify(cache, times(1)).getUpdateTime("CACHE_TIMESTAMP");
    }

    @Test
    void testUpdateTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        carParkSyncUpSession.updateTimestamp(now);
        verify(cache, times(1)).putUpdateTime("CACHE_TIMESTAMP", now);
    }
}