package com.nam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "car_park_info")
public class CarParkInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String lotType;
    private int totalLots;
    private int lotsAvailable;

    @ManyToOne
    @JoinColumn(name = "carpark_number")
    private CarPark carPark;
}
