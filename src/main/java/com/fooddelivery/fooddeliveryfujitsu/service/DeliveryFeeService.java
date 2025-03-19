package com.fooddelivery.fooddeliveryfujitsu.service;

import com.fooddelivery.fooddeliveryfujitsu.dto.DeliveryFeeRequestDto;
import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.entity.StationFee;
import com.fooddelivery.fooddeliveryfujitsu.entity.Weather;
import com.fooddelivery.fooddeliveryfujitsu.enums.City;
import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import com.fooddelivery.fooddeliveryfujitsu.exception.ForbiddenVehicleUsageException;
import com.fooddelivery.fooddeliveryfujitsu.exception.NotFoundException;
import com.fooddelivery.fooddeliveryfujitsu.repository.StationFeeRepository;
import com.fooddelivery.fooddeliveryfujitsu.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DeliveryFeeService {

    private final WeatherRepository weatherRepository;
    private final StationFeeRepository stationFeeRepository;
    private final StationService stationService;
    private static final Logger log = LoggerFactory.getLogger(DeliveryFeeService.class);


    public double calculateDeliveryFee(DeliveryFeeRequestDto request, Optional<LocalDateTime> time) {
        City city = request.getCity();
        VehicleType vehicleType = request.getVehicleType();
        String stationName = stationService.cityToStationName(city);
        Station station = stationService.stationNameToStation(stationName);

        log.info("Fetching fees for city: {} and vehicle type: {}", city, vehicleType);

        StationFee stationFee = stationFeeRepository.findByStationAndVehicleType(station, vehicleType)
                .orElseThrow(() -> new NotFoundException("No fee configuration found for city: " + city + " and vehicle type: " + vehicleType));

        Weather weather = time.isPresent() ? findClosestWeather(station, time.get()) : findLatestWeather(station);


        log.info("\nWeather info for station {} at {} was: \nTemperature: {} \nWind speed: {} \nWeather phenomenon: {}",
                weather.getStation().getStationName(), weather.getTimestamp(), weather.getAirTemperature(), weather.getWindSpeed(), weather.getWeatherPhenomenon());

        // Regional Base Fee, Air Temperature Extra Fee, Wind Speed Extra Fee, Weather phenomenon extra free
        double rbf = stationFee.getBaseFee().doubleValue();
        double atef = calculateTemperatureExtraFee(weather.getAirTemperature(), stationFee);
        double wsef = calculateWindSpeedExtraFee(weather.getWindSpeed(), vehicleType, stationFee);
        double wpef = calculateWeatherPhenomenonExtraFee(weather.getWeatherPhenomenon(), vehicleType, stationFee);

        double total = rbf + atef + wsef + wpef;
        log.info("Total for delivery in {} with vehicle type {} and date {} is: {} €", city, vehicleType, weather.getTimestamp(), total);
        return total;
    }

    private Weather findLatestWeather(Station station) {
        return weatherRepository.findTopByStationOrderByTimestampDesc(station)
                .orElseThrow(() -> new NotFoundException("No weather data found for station: " + station.getStationName()));
    }

    private Weather findClosestWeather(Station station, LocalDateTime time) {
        return weatherRepository.findAllWeatherForStation(station)
                .stream()
                .min(Comparator.comparingLong(w -> Math.abs(Duration.between(w.getTimestamp(), time).toSeconds())))
                .orElseThrow(() -> new NotFoundException("No weather data found for station: " + station.getStationName() + " around time: " + time));
    }

    private double calculateTemperatureExtraFee(Double airTemperature, StationFee stationFee) {
        if (stationFee.getTemperatureBelowMinus10Fee() != null && airTemperature < -10) {
            return stationFee.getTemperatureBelowMinus10Fee().doubleValue();
        }
        if (stationFee.getTemperatureBetweenMinus10And0Fee() != null && airTemperature >= -10 && airTemperature < 0) {
            return stationFee.getTemperatureBetweenMinus10And0Fee().doubleValue();
        }
        return 0.0;
    }

    private double calculateWindSpeedExtraFee(Double windSpeed, VehicleType vehicleType, StationFee stationFee) {
        if (windSpeed > 20 && (vehicleType == VehicleType.BIKE || vehicleType == VehicleType.SCOOTER)) {
            throw new ForbiddenVehicleUsageException("Usage of selected vehicle type is forbidden due to high wind speeds.");
        }
        if (stationFee.getWindSpeedBetween10And20Fee() != null && windSpeed >= 10 && windSpeed <= 20) {
            return stationFee.getWindSpeedBetween10And20Fee().doubleValue();
        }
        return 0.0;
    }

    private double calculateWeatherPhenomenonExtraFee(String phenomenon, VehicleType vehicleType, StationFee stationFee) {
        if (phenomenon != null) {
            String lowerPhenomenon = phenomenon.toLowerCase();
            if (stationFee.getWeatherPhenomenonSnowSleetFee() != null && (lowerPhenomenon.contains("snow") || lowerPhenomenon.contains("sleet"))) {
                return stationFee.getWeatherPhenomenonSnowSleetFee().doubleValue();
            }
            if (stationFee.getWeatherPhenomenonRainFee() != null && lowerPhenomenon.contains("rain")) {
                return stationFee.getWeatherPhenomenonRainFee().doubleValue();
            }
            if ((lowerPhenomenon.contains("glaze") || lowerPhenomenon.contains("hail") || lowerPhenomenon.contains("thunder")) &&
                    (vehicleType == VehicleType.BIKE || vehicleType == VehicleType.SCOOTER)) {
                throw new ForbiddenVehicleUsageException("Usage of selected vehicle type is forbidden due to extreme weather.");
            }
        }
        return 0.0;
    }
}
