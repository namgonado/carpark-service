package com.nam.carpark.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "car_park")
public class CarPark {
    @Id
    private String carparkNumber;
    private LocalDateTime updateDatetime;

    @OneToMany(mappedBy = "carPark", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarParkInfo> carParkInfos;
}
