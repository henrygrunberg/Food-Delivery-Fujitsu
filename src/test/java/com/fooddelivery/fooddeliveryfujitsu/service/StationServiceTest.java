package com.fooddelivery.fooddeliveryfujitsu.service;

import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.enums.City;
import com.fooddelivery.fooddeliveryfujitsu.exception.InvalidCityException;
import com.fooddelivery.fooddeliveryfujitsu.exception.NotFoundException;
import com.fooddelivery.fooddeliveryfujitsu.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    private Station station;

    @BeforeEach
    void setUp() {
        station = new Station();
        station.setStationName("Tallinn-Harku");
    }

    @Test
    void stationNameToStation_ShouldReturnStation_WhenStationExists() {
        when(stationRepository.findByStationName("Tallinn-Harku")).thenReturn(Optional.of(station));

        Station result = stationService.stationNameToStation("Tallinn-Harku");

        assertNotNull(result);
        assertEquals("Tallinn-Harku", result.getStationName());
        verify(stationRepository).findByStationName("Tallinn-Harku");
    }

    @Test
    void stationNameToStation_ShouldThrowNotFoundException_WhenStationDoesNotExist() {
        when(stationRepository.findByStationName("InvalidStation")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> stationService.stationNameToStation("InvalidStation"));

        assertTrue(exception.getMessage().contains("No station found with name: InvalidStation"));
    }

    @Test
    void cityToStationName_ShouldReturnCorrectStationName_ForTallinn() {
        String result = stationService.cityToStationName(City.TALLINN);
        assertEquals("Tallinn-Harku", result);
    }

    @Test
    void cityToStationName_ShouldReturnCorrectStationName_ForTartu() {
        String result = stationService.cityToStationName(City.TARTU);
        assertEquals("Tartu-Tõravere", result);
    }

    @Test
    void cityToStationName_ShouldReturnCorrectStationName_ForParnu() {
        String result = stationService.cityToStationName(City.PARNU);
        assertEquals("Pärnu", result);
    }

    @Test
    void cityToStationName_ShouldThrowInvalidCityException_IfCityIsNull() {
        InvalidCityException exception = assertThrows(InvalidCityException.class,
                () -> stationService.cityToStationName(null));

        assertTrue(exception.getMessage().contains("Invalid city provided"));
    }
}
