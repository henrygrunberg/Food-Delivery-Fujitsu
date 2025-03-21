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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationFeeServiceTest {

    @Mock
    private StationFeeRepository stationFeeRepository;

    @Mock
    private StationFeeMapping stationFeeMapping;

    @Mock
    private StationService stationService;

    @InjectMocks
    private StationFeeService stationFeeService;

    private Station station;
    private StationFee stationFee;
    private StationFeeDto stationFeeDto;

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
        stationFee.setStation(station);
        stationFee.setVehicleType(VehicleType.BIKE);

        stationFeeDto = new StationFeeDto();
    }

    @Test
    void getAllStationFees_ShouldReturnFees_WhenDataExists() {
        when(stationFeeRepository.findAll()).thenReturn(List.of(stationFee));
        when(stationFeeMapping.entitysToDtos(List.of(stationFee))).thenReturn(List.of(stationFeeDto));

        List<StationFeeDto> result = stationFeeService.getAllStationFees();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(stationFeeRepository).findAll();
        verify(stationFeeMapping).entitysToDtos(List.of(stationFee));
    }

    @Test
    void getAllStationFees_ShouldThrowNotFound_WhenNoFeesExist() {
        when(stationFeeRepository.findAll()).thenReturn(List.of());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> stationFeeService.getAllStationFees());

        assertTrue(exception.getMessage().contains("No station fees found"));
    }

    @Test
    void getStationFeesByCity_ShouldReturnFees_WhenCityIsValid() {
        Station mockStation = mock(Station.class);

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(mockStation);
        when(mockStation.getStationFees()).thenReturn(List.of(stationFee));
        when(stationFeeMapping.entitysToDtos(List.of(stationFee))).thenReturn(List.of(stationFeeDto));

        List<StationFeeDto> result = stationFeeService.getStationFeesByCity("TALLINN");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(stationService).cityToStationName(City.TALLINN);
        verify(stationService).stationNameToStation("Tallinn-Harku");
        verify(mockStation).getStationFees();
        verify(stationFeeMapping).entitysToDtos(List.of(stationFee));
    }

    @Test
    void getStationFeesByCity_ShouldThrowNotFound_WhenNoFeesExist() {
        Station mockStation = mock(Station.class);

        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(mockStation);
        when(mockStation.getStationFees()).thenReturn(List.of()); // No fees

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> stationFeeService.getStationFeesByCity("TALLINN"));

        assertTrue(exception.getMessage().contains("No station fees found"));
        verify(stationService).cityToStationName(City.TALLINN);
        verify(stationService).stationNameToStation("Tallinn-Harku");
        verify(mockStation).getStationFees();
    }

    @Test
    void getStationFeesByCity_ShouldThrowInvalidCityException_WhenCityIsInvalid() {
        InvalidCityException exception = assertThrows(InvalidCityException.class,
                () -> stationFeeService.getStationFeesByCity("INVALIDCITY"));

        assertTrue(exception.getMessage().contains("Invalid city name"));
    }

    @Test
    void getStationFeesByCityAndVehicleType_ShouldReturnFee_WhenValidInput() {
        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE)).thenReturn(Optional.of(stationFee));
        when(stationFeeMapping.entityToDto(stationFee)).thenReturn(stationFeeDto);

        StationFeeDto result = stationFeeService.getStationFeesByCityAndVehicleType("TALLINN", "BIKE");

        assertNotNull(result);
        verify(stationFeeRepository).findByStationAndVehicleType(station, VehicleType.BIKE);
    }

    @Test
    void getStationFeesByCityAndVehicleType_ShouldThrowInvalidVehicleTypeException_WhenVehicleTypeIsInvalid() {
        InvalidVehicleTypeException exception = assertThrows(InvalidVehicleTypeException.class,
                () -> stationFeeService.getStationFeesByCityAndVehicleType("TALLINN", "INVALIDVEHICLE"));

        assertTrue(exception.getMessage().contains("Invalid vehicle type"));
    }

    @Test
    void getStationFeesByCityAndVehicleType_ShouldThrowNotFound_WhenNoFeesExist() {
        when(stationService.cityToStationName(City.TALLINN)).thenReturn("Tallinn-Harku");
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> stationFeeService.getStationFeesByCityAndVehicleType("TALLINN", "BIKE"));

        assertTrue(exception.getMessage().contains("No station fees found"));
    }

    @Test
    void updateStationFee_ShouldUpdateFee_WhenValidData() {
        StationFeeUpdateDto updateDto = new StationFeeUpdateDto("Tallinn-Harku", VehicleType.BIKE, 4.0, 1.5, 0.7, 0.6, 1.0, 0.5);
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE)).thenReturn(Optional.of(stationFee));
        when(stationFeeRepository.save(stationFee)).thenReturn(stationFee);
        when(stationFeeMapping.entityToDto(stationFee)).thenReturn(stationFeeDto);

        StationFeeDto result = stationFeeService.updateStationFee(updateDto);

        assertNotNull(result);
        verify(stationFeeRepository).save(stationFee);
    }

    @Test
    void updateStationFee_ShouldThrowNotFound_WhenFeeDoesNotExist() {
        StationFeeUpdateDto updateDto = new StationFeeUpdateDto("Tallinn-Harku", VehicleType.BIKE, 4.0, 1.5, 0.7, 0.6, 1.0, 0.5);
        when(stationService.stationNameToStation("Tallinn-Harku")).thenReturn(station);
        when(stationFeeRepository.findByStationAndVehicleType(station, VehicleType.BIKE)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> stationFeeService.updateStationFee(updateDto));

        assertTrue(exception.getMessage().contains("No station fee found"));
    }
}
