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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryFeeServiceTest {

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private StationFeeRepository stationFeeRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private DeliveryFeeService deliveryFeeService;

    private Station station;
    private StationFee stationFee;
    private Weather weather;
    private DeliveryFeeRequestDto requestDto;

    @BeforeEach
    void setUp() {
        station = new Station();
        station.setStationName("Tallinn-Harku");

        stationFee = new StationFee();
        stationFee.setBaseFee(3.0);
        stationFee.setTemperatureBelowMinus10Fee(1.0);
        stationFee.setTemperatureBetweenMinus10And0Fee(0.5);
        stationFee.setWindSpeedBetween10And20Fee(0.5);
        stationFee.setWeatherPhenomenonSnowSleetFee(1.0);
        stationFee.setWeatherPhenomenonRainFee(0.5);

        weather = new Weather();
        weather.setAirTemperature(-5.0);
        weather.setWindSpeed(15.0);
        weather.setWeatherPhenomenon("Moderate snowfall");
        weather.setStation(station);
        weather.setTimestamp(LocalDateTime.now());

        requestDto = new DeliveryFeeRequestDto(City.TALLINN, VehicleType.BIKE, null);
    }

    @Test
    void calculateDeliveryFee_ShouldReturnCorrectFee_WhenWeatherDataExists() {
        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.of(weather));

        double result = deliveryFeeService.calculateDeliveryFee(requestDto, Optional.empty());

        double expectedFee = 3.0 + 0.5 + 0.5 + 1.0; // Base Fee + Temperature Fee + Wind Speed Fee + Snow Fee
        assertEquals(expectedFee, result);
    }

    @Test
    void calculateDeliveryFee_ShouldThrowNotFoundException_WhenNoWeatherData() {
        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.empty());

        Optional<LocalDateTime> time = Optional.empty();
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> deliveryFeeService.calculateDeliveryFee(requestDto, time));

        assertTrue(exception.getMessage().contains("No weather data found for station"));
    }

    @Test
    void calculateDeliveryFee_ShouldThrowForbiddenVehicleUsageException_WhenHighWindSpeed() {
        weather.setWindSpeed(21.0);

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.of(weather));

        Optional<LocalDateTime> time = Optional.empty();
        ForbiddenVehicleUsageException exception = assertThrows(ForbiddenVehicleUsageException.class,
                () -> deliveryFeeService.calculateDeliveryFee(requestDto, time));

        assertTrue(exception.getMessage().contains("Usage of selected vehicle type is forbidden due to high wind speeds."));
    }

    @Test
    void calculateDeliveryFee_ShouldThrowNotFoundException_WhenStationFeeNotFound() {
        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.empty());

        Optional<LocalDateTime> time = Optional.empty();
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> deliveryFeeService.calculateDeliveryFee(requestDto, time));

        assertTrue(exception.getMessage().contains("No fee configuration found for city"));
    }

    @Test
    void calculateDeliveryFee_ShouldIncludeExtraFees_ForRain() {
        weather.setWeatherPhenomenon("Moderate rain");
        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.of(weather));

        double result = deliveryFeeService.calculateDeliveryFee(requestDto, Optional.empty());

        double expectedFee = 3.0 + 0.5 + 0.5 + 0.5; // Base + Temperature + Wind + Rain
        assertEquals(expectedFee, result);
    }

    @Test
    void calculateDeliveryFee_ShouldFindClosestWeatherData() {
        LocalDateTime requestedTime = LocalDateTime.now().minusHours(3);
        Weather pastWeather = new Weather();
        pastWeather.setAirTemperature(-3.0);
        pastWeather.setWindSpeed(12.0);
        pastWeather.setWeatherPhenomenon("Few clouds");
        pastWeather.setStation(station);
        pastWeather.setTimestamp(requestedTime);

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findAllWeatherForStation(station))
                .thenReturn(List.of(pastWeather));

        double result = deliveryFeeService.calculateDeliveryFee(requestDto, Optional.of(requestedTime));

        double expectedFee = 3.0 + 0.5 + 0.5; // Base + Temperature + Wind
        assertEquals(expectedFee, result);
    }

    @Test
    void calculateDeliveryFee_ShouldThrowNotFoundException_WhenNoWeatherDataAvailableForTime() {
        LocalDateTime requestedTime = LocalDateTime.now().minusDays(1);

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findAllWeatherForStation(station))
                .thenReturn(List.of()); // No weather data

        Optional<LocalDateTime> time = Optional.of(requestedTime);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> deliveryFeeService.calculateDeliveryFee(requestDto, time));

        assertTrue(exception.getMessage().contains("No weather data found for station"));
    }

    @Test
    void calculateDeliveryFee_ShouldThrowForbiddenVehicleUsageException_WhenHailAndScooter() {
        weather.setWeatherPhenomenon("Hail");
        requestDto = new DeliveryFeeRequestDto(City.TALLINN, VehicleType.SCOOTER, null);

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.SCOOTER))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.of(weather));

        Optional<LocalDateTime> time = Optional.empty();
        ForbiddenVehicleUsageException exception = assertThrows(ForbiddenVehicleUsageException.class,
                () -> deliveryFeeService.calculateDeliveryFee(requestDto, time));

        assertTrue(exception.getMessage().contains("Usage of selected vehicle type is forbidden due to extreme weather."));
    }

    @Test
    void calculateDeliveryFee_ShouldReturnBaseFee_WhenWindSpeedBelow10_NoExtraFee() {
        weather.setWindSpeed(5.0); // No wind extra fee applied

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.of(weather));

        double result = deliveryFeeService.calculateDeliveryFee(requestDto, Optional.empty());

        double expectedFee = 3.0 + 0.5 + 1.0; // Base + Temperature + Snow Fee
        assertEquals(expectedFee, result);
    }

    @Test
    void calculateDeliveryFee_ShouldReturnBaseFee_WhenTemperatureAbove0_NoExtraFee() {
        weather.setAirTemperature(5.0); // Above 0°C, no extra temperature fee should apply

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.of(weather));

        double result = deliveryFeeService.calculateDeliveryFee(requestDto, Optional.empty());

        double expectedFee = 3.0 + 0.0 + 0.5 + 1.0; // Base + Temperature (0) + Wind + Snow
        assertEquals(expectedFee, result);
    }

    @Test
    void calculateDeliveryFee_ShouldApplyTemperatureFee_WhenTemperatureBelowMinus15() {
        weather.setAirTemperature(-15.0);

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE))
                .thenReturn(Optional.of(stationFee));
        when(weatherRepository.findTopByStationOrderByTimestampDesc(station))
                .thenReturn(Optional.of(weather));

        double result = deliveryFeeService.calculateDeliveryFee(requestDto, Optional.empty());

        double expectedFee = 3.0 + 1.0 + 0.5 + 1.0; // Base + Temperature (-15°C) + Wind + Snow
        assertEquals(expectedFee, result);
    }
}
