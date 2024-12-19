package com.nam.carpark.controller;

import com.nam.carpark.model.dto.SearchCarParkResponse;
import com.nam.carpark.provider.availability.CarParkAvailabilityProvider;
import com.nam.carpark.service.CarParkSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carparks")
public class CarParkController {

    @Autowired
    private CarParkAvailabilityProvider carParkInfoProvider;

    @PostMapping("/sync")
    public void syncCarParkInfo() {
        carParkInfoProvider.poll();
    }

    @Autowired
    private CarParkSearchService carParkSearchService;

    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestCarParks(
            @RequestParam(required = true) Double latitude,
            @RequestParam(required = true) Double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage) {

        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().body("Latitude and Longitude are required parameters.");
        }

        List<SearchCarParkResponse> carParks = carParkSearchService.findNearestCarParks(latitude, longitude, page, perPage);
        return ResponseEntity.ok(carParks);
    }
}
