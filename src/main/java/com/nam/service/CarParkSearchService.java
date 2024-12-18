package com.nam.service;

import com.nam.model.dto.CarParkAvailabilityResponse;
import com.nam.model.dto.SearchCarParkResponse;

import java.util.List;

public interface CarParkSearchService {
    List<SearchCarParkResponse> findNearestCarParks(double latitude, double longitude, int page, int perPage);
}