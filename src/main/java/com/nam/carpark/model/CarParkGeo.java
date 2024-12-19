package com.nam.carpark.model;

import lombok.Data;

@Data
public class CarParkGeo {
    private String carParkNo;
    private String address;
    private double lat;
    private double lon;
    private String carParkType;
    private String typeOfParkingSystem;
    private String shortTermParking;
    private String freeParking;
    private boolean nightParking;
    private int carParkDecks;
    private double gantryHeight;
    private boolean carParkBasement;
}
