package com.nam.carpark.service;

import com.nam.carpark.model.dto.SearchCarParkResponse;

import java.util.List;

public interface CarParkSearchService {
    List<SearchCarParkResponse> findNearestCarParks(double latitude, double longitude, int page, int perPage);
}