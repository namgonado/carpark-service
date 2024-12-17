package com.nam.repository;

import com.nam.model.CarPark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarParkRepository extends JpaRepository<CarPark, String> {
}