package com.nam.carpark.repository;

import com.nam.carpark.model.CarPark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarParkRepository extends JpaRepository<CarPark, String> {
}