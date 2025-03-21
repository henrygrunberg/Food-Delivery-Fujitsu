package com.fooddelivery.fooddeliveryfujitsu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.fooddeliveryfujitsu.dto.StationFeeUpdateDto;
import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Order(2)
@SpringBootTest
@AutoConfigureMockMvc
public class StationFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllStationFees() throws Exception {
        mockMvc.perform(get("/api/station-fees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)));
    }

    @Test
    void shouldReturnFeesByCity() throws Exception {
        mockMvc.perform(get("/api/station-fees/TALLINN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void shouldReturnFeesByCityAndVehicleType() throws Exception {
        mockMvc.perform(get("/api/station-fees/TALLINN/BIKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName").value("Tallinn-Harku"))
                .andExpect(jsonPath("$.vehicleType").value("BIKE"));
    }

    @Test
    void shouldThrowForInvalidCity() throws Exception {
        mockMvc.perform(get("/api/station-fees/MOSCOW"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid city name: MOSCOW"));
    }

    @Test
    void shouldThrowForInvalidVehicleType() throws Exception {
        mockMvc.perform(get("/api/station-fees/TALLINN/PLANE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid vehicle type: PLANE"));
    }

    @Test
    void shouldThrowWhenCityVehicleTypeCombinationNotFound() throws Exception {
        mockMvc.perform(get("/api/station-fees/PARNU/PLANE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid vehicle type: PLANE"));
    }

    @Test
    void shouldUpdateStationFeeSuccessfully() throws Exception {
        StationFeeUpdateDto updateDto = new StationFeeUpdateDto();
        updateDto.setStationName("Tallinn-Harku");
        updateDto.setVehicleType(VehicleType.BIKE);
        updateDto.setBaseFee(4.99);
        updateDto.setTemperatureBelowMinus10Fee(1.11);
        updateDto.setTemperatureBetweenMinus10And0Fee(0.22);
        updateDto.setWindSpeedBetween10And20Fee(0.33);
        updateDto.setWeatherPhenomenonSnowSleetFee(0.44);
        updateDto.setWeatherPhenomenonRainFee(0.55);

        mockMvc.perform(put("/api/station-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationName").value("Tallinn-Harku"))
                .andExpect(jsonPath("$.vehicleType").value("BIKE"))
                .andExpect(jsonPath("$.baseFee").value(4.99))
                .andExpect(jsonPath("$.temperatureBelowMinus10Fee").value(1.11))
                .andExpect(jsonPath("$.temperatureBetweenMinus10And0Fee").value(0.22))
                .andExpect(jsonPath("$.windSpeedBetween10And20Fee").value(0.33))
                .andExpect(jsonPath("$.weatherPhenomenonSnowSleetFee").value(0.44))
                .andExpect(jsonPath("$.weatherPhenomenonRainFee").value(0.55));
    }

    @Test
    void shouldFailToUpdate_whenStationDoesNotExist() throws Exception {
        StationFeeUpdateDto updateDto = new StationFeeUpdateDto();
        updateDto.setStationName("Nonexistent-Station");
        updateDto.setVehicleType(VehicleType.BIKE);
        updateDto.setBaseFee(3.5);

        mockMvc.perform(put("/api/station-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("No station found")));
    }

    @Test
    void shouldFailValidation_onEmptyPayload() throws Exception {
        String emptyPayload = "{}";

        mockMvc.perform(put("/api/station-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("must not be empty")));
    }

    @Test
    void shouldFailValidation_onOutOfRangeFee() throws Exception {
        StationFeeUpdateDto updateDto = new StationFeeUpdateDto();
        updateDto.setStationName("Tallinn-Harku");
        updateDto.setVehicleType(VehicleType.BIKE);
        updateDto.setBaseFee(11.0);

        mockMvc.perform(put("/api/station-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Base fee cannot be more than 10.00€")));
    }
}
