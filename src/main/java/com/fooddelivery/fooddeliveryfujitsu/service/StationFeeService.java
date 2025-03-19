package com.fooddelivery.fooddeliveryfujitsu.service;

import com.fooddelivery.fooddeliveryfujitsu.dto.StationFeeDto;
import com.fooddelivery.fooddeliveryfujitsu.dto.StationFeeUpdateDto;
import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.entity.StationFee;
import com.fooddelivery.fooddeliveryfujitsu.enums.City;
import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import com.fooddelivery.fooddeliveryfujitsu.exception.InvalidCityException;
import com.fooddelivery.fooddeliveryfujitsu.exception.InvalidVehicleTypeException;
import com.fooddelivery.fooddeliveryfujitsu.exception.NotFoundException;
import com.fooddelivery.fooddeliveryfujitsu.mapping.StationFeeMapping;
import com.fooddelivery.fooddeliveryfujitsu.repository.StationFeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StationFeeService {

    private final StationFeeRepository stationFeeRepository;
    private final StationFeeMapping stationFeeMapping;
    private final StationService stationService;
    private static final Logger log = LoggerFactory.getLogger(StationFeeService.class);


    public List<StationFeeDto> getAllStationFees() {
        log.info("Fetching all station fees");
        List<StationFee> stationFees = stationFeeRepository.findAll();
        if (stationFees.isEmpty()) throw new NotFoundException("No station fees found");
        return stationFeeMapping.entitysToDtos(stationFees);
    }

    public List<StationFeeDto> getStationFeesByCity(String city) {
        log.info("Searching for station fees in {}", city);
        City cityEnum = cityToEnum(city);
        String stationName = stationService.cityToStationName(cityEnum);
        Station station = stationService.stationNameToStation(stationName);

        log.info("Fetching all station fees for station {}", stationName);
        List<StationFee> stationFees = station.getStationFees();
        if (stationFees.isEmpty()) throw new NotFoundException("No station fees found for station: " + stationName);
        return stationFeeMapping.entitysToDtos(stationFees);
    }

    public StationFeeDto getStationFeesByCityAndVehicleType(String city, String vehicleType) {
        log.info("Searching for station fees in {} for vehicle type {}", city, vehicleType);
        City cityEnum = cityToEnum(city);
        VehicleType vehicleTypeEnum = vehicleTypeToEnum(vehicleType);
        String stationName = stationService.cityToStationName(cityEnum);
        Station station = stationService.stationNameToStation(stationName);
        log.info("Fetching all station fees for station {} and vehicle type {}", stationName, vehicleType);
        StationFee stationFee = stationFeeRepository.findByStationAndVehicleType(station, vehicleTypeEnum)
                .orElseThrow(() -> new NotFoundException("No station fees found for station: " + stationName + " and vehicle type: " + vehicleType));
        return stationFeeMapping.entityToDto(stationFee);
    }

    private City cityToEnum(String city) {
        try {
            return City.valueOf(city.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCityException("Invalid city name: " + city);
        }
    }

    private VehicleType vehicleTypeToEnum(String vehicleType) {
        try {
            return VehicleType.valueOf(vehicleType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidVehicleTypeException("Invalid vehicle type: " + vehicleType);
        }
    }

    public StationFeeDto updateStationFee(StationFeeUpdateDto updateDto) {
        String stationName = updateDto.getStationName();
        Station station = stationService.stationNameToStation(stationName);
        VehicleType vehicleType = updateDto.getVehicleType();
        log.info("Updating station fee for station {} and vehicle type {}", stationName, vehicleType);

        StationFee stationFee = stationFeeRepository.findByStationAndVehicleType(station, vehicleType)
                .orElseThrow(() -> new NotFoundException("No station fee found for station: " + station
                        + " and vehicle type: " + vehicleType));

        if (updateDto.getBaseFee() != null) stationFee.setBaseFee(updateDto.getBaseFee());
        if (updateDto.getTemperatureBelowMinus10Fee() != null) stationFee.setTemperatureBelowMinus10Fee(updateDto.getTemperatureBelowMinus10Fee());
        if (updateDto.getTemperatureBetweenMinus10And0Fee() != null) stationFee.setTemperatureBetweenMinus10And0Fee(updateDto.getTemperatureBetweenMinus10And0Fee());
        if (updateDto.getWindSpeedBetween10And20Fee() != null) stationFee.setWindSpeedBetween10And20Fee(updateDto.getWindSpeedBetween10And20Fee());
        if (updateDto.getWeatherPhenomenonSnowSleetFee() != null) stationFee.setWeatherPhenomenonSnowSleetFee(updateDto.getWeatherPhenomenonSnowSleetFee());
        if (updateDto.getWeatherPhenomenonRainFee() != null) stationFee.setWeatherPhenomenonRainFee(updateDto.getWeatherPhenomenonRainFee());

        stationFee = stationFeeRepository.save(stationFee);
        log.info("Successfully updated station fee for station {} and vehicle type {}", updateDto.getStationName(), updateDto.getVehicleType());
        return stationFeeMapping.entityToDto(stationFee);
    }
}
