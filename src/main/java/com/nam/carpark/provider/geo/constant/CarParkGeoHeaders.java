package com.nam.carpark.provider.geo.constant;

public enum CarParkGeoHeaders {
    CAR_PARK_NO("car_park_no"),
    ADDRESS("address"),
    X_COORD("x_coord"),
    Y_COORD("y_coord"),
    CAR_PARK_TYPE("car_park_type"),
    TYPE_OF_PARKING_SYSTEM("type_of_parking_system"),
    SHORT_TERM_PARKING("short_term_parking"),
    FREE_PARKING("free_parking"),
    NIGHT_PARKING("night_parking"),
    CAR_PARK_DECKS("car_park_decks"),
    GANTRY_HEIGHT("gantry_height"),
    CAR_PARK_BASEMENT("car_park_basement");

    private final String header;

    CarParkGeoHeaders(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}