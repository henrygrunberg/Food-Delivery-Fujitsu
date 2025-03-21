package com.fooddelivery.fooddeliveryfujitsu.service;

import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.enums.City;
import com.fooddelivery.fooddeliveryfujitsu.exception.InvalidCityException;
import com.fooddelivery.fooddeliveryfujitsu.exception.NotFoundException;
import com.fooddelivery.fooddeliveryfujitsu.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;
    private static final Logger log = LoggerFactory.getLogger(StationService.class);

    public Station stationNameToStation(String stationName) {
        return stationRepository.findByStationName(stationName)
                .orElseThrow(() -> new NotFoundException("No station found with name: " + stationName));
    }

    public String cityToStationName(City city) {
        if (city == null) {
            throw new InvalidCityException("Invalid city provided: null");
        }

        log.info("Converting city {} to station name", city);
        return switch (city) {
            case TALLINN -> "Tallinn-Harku";
            case TARTU -> "Tartu-Tõravere";
            case PARNU -> "Pärnu";
        };
    }
}
