package com.nam.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinateConverterTest {

    @Test
    public void testDistanceBetweenHCMAndDaNang() {
        // Coordinates for Ho Chi Minh City
        double hcmLat = 10.8231;
        double hcmLon = 106.6297;

        // Coordinates for Da Nang City
        double daNangLat = 16.0471;
        double daNangLon = 108.2068;

        // Calculate distance
        double distance = CoordinateConverter.distance(hcmLat, hcmLon, daNangLat, daNangLon);

        // Expected distance in kilometers (approximate) by https://www.nhc.noaa.gov/gccalc.shtml
        double expectedDistance = 605.0;

        // Allow a small margin of error
        double delta = 1.0;

        assertEquals(expectedDistance, distance, delta);
    }

    @Test
    public void testDistanceBetweenHCMAndHanoi() {
        // Coordinates for Ho Chi Minh City
        double hcmLat = 10.8231;
        double hcmLon = 106.6297;

        // Coordinates for Hanoi City
        double hanoiLat = 21.0285;
        double hanoiLon = 105.8542;

        // Calculate distance
        double distance = CoordinateConverter.distance(hcmLat, hcmLon, hanoiLat, hanoiLon);

        // Expected distance in kilometers (approximate) by https://www.nhc.noaa.gov/gccalc.shtml
        double expectedDistance = 1137.0;

        // Allow a small margin of error
        double delta = 1.0;

        assertEquals(expectedDistance, distance, delta);
    }
}
