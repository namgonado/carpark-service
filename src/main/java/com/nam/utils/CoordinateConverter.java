package com.nam.utils;

import com.thirparty.net.qxcg.svy21.LatLonCoordinate;
import com.thirparty.net.qxcg.svy21.SVY21;
import com.thirparty.net.qxcg.svy21.SVY21Coordinate;

public class CoordinateConverter {
    // Earth radius in kilometers
    private static final double EARTH_RADIUS = 6371.0;
    public static LatLonCoordinate toLatLong(double norther, double easter) {

        // Create an SVY21Coordinate object
        SVY21Coordinate svy21Coord = new SVY21Coordinate(norther, easter);

        // Convert to latitude and longitude
        LatLonCoordinate latLonCoord = SVY21.computeLatLon(svy21Coord);
        return latLonCoord;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Differences in coordinates
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        return EARTH_RADIUS * c;
    }
}
