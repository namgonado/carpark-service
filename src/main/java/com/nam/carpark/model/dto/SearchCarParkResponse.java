package com.nam.carpark.model.dto;

import lombok.Data;

@Data
public class SearchCarParkResponse {
    private String carParkNumber;
    private String address;
    private double latitude;
    private double longitude;
    private int totalLots;
    private int availableLots;
    private double distance;
}