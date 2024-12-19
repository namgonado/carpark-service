package com.nam.carpark.service;

import com.nam.carpark.model.CarPark;
import com.nam.carpark.model.CarParkGeo;
import com.nam.carpark.model.dto.SearchCarParkResponse;
import com.nam.carpark.provider.geo.CarParkGeoProvider;
import com.nam.carpark.repository.CarParkRepository;
import com.nam.carpark.utils.CoordinateConverter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarParkSearchServiceImpl implements CarParkSearchService {

    @Autowired
    private CarParkGeoProvider carParkGeoProvider;

    @Autowired
    private CarParkRepository carParkRepository;

    @Override
    @Transactional
    public List<SearchCarParkResponse> findNearestCarParks(double latitude, double longitude, int page, int perPage) {
        List<CarParkGeo> allCarParks = carParkGeoProvider.getAllCarPark();

        List<CarParkGeo> sortedCarParks = allCarParks.stream()
                .sorted(Comparator.comparingDouble(carPark -> CoordinateConverter.distance(latitude, longitude, carPark.getLat(), carPark.getLon())))
                .collect(Collectors.toList());

        int start = page * perPage;
        int end = Math.min(start + perPage, sortedCarParks.size());

        return sortedCarParks.subList(start, end).stream()
                .map(carParkGeo -> {
                    CarPark carPark = carParkRepository.findById(carParkGeo.getCarParkNo()).orElse(null);
                    if (carPark != null) {
                        SearchCarParkResponse response = new SearchCarParkResponse();
                        response.setCarParkNumber(carParkGeo.getCarParkNo());
                        response.setAddress(carParkGeo.getAddress());
                        response.setLatitude(carParkGeo.getLat());
                        response.setLongitude(carParkGeo.getLon());
                        response.setTotalLots(carPark.getCarParkInfos().stream().mapToInt(info -> info.getTotalLots()).sum());
                        response.setAvailableLots(carPark.getCarParkInfos().stream().mapToInt(info -> info.getLotsAvailable()).sum());

                        double distance = CoordinateConverter.distance(latitude, longitude, carParkGeo.getLat(), carParkGeo.getLon());
                        response.setDistance(distance);
                        return response;
                    }
                    return null;
                })
                .filter(response -> response != null && response.getAvailableLots() > 0)
                .collect(Collectors.toList());
    }
}
