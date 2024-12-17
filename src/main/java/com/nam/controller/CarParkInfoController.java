package com.nam.controller;

import com.nam.provider.carpark.availability.CarParkProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carpark")
public class CarParkInfoController {

    @Autowired
    private CarParkProvider carParkInfoProvider;

    @PostMapping("/sync")
    public void syncCarParkInfo() {
        carParkInfoProvider.poll();
    }
}
