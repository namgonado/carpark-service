package com.nam.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
