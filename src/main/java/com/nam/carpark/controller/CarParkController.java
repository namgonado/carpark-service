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

    @Autowired(required = false)
    private CarParkAvailabilityProvider carParkInfoProvider;
    @Autowired
    private CarParkSearchService carParkSearchService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncCarParkInfo() {
        if (carParkInfoProvider == null) {
            return ResponseEntity.status(500).body("Polling feature is not available");
        }
        carParkInfoProvider.poll();
        return ResponseEntity.ok("Car Park Polling sync up has been complete");
    }

    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestCarParks(
            @RequestParam(required = true) Double latitude,
            @RequestParam(required = true) Double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "per_page", defaultValue = "10") int perPage) {

        List<SearchCarParkResponse> carParks = carParkSearchService.findNearestCarParks(latitude, longitude, page, perPage);
        return ResponseEntity.ok(carParks);
    }
}
