package com.fooddelivery.fooddeliveryfujitsu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.fooddeliveryfujitsu.dto.DeliveryFeeRequestDto;
import com.fooddelivery.fooddeliveryfujitsu.enums.City;
import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Order(1)
@SpringBootTest
@AutoConfigureMockMvc
public class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnBaseFee_only() throws Exception {
        // Temp: 5°C, Wind: 10m/s, Clear
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.TALLINN,
                VehicleType.CAR,
                LocalDateTime.of(2025, 3, 21, 10, 15)
        );

        // Base fee: 4.0
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFee").value(4.0));
    }

    @Test
    void shouldThrowForbiddenException_dueToWindSpeedOver20() throws Exception {
        // Temp: -15°C, Wind: 25m/s, Blizzard
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.TALLINN,
                VehicleType.BIKE,
                LocalDateTime.of(2025, 3, 21, 11, 15)
        );

        // ForbiddenVehicleUsageException, Wind > 20m/s
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("forbidden")));
    }

    @Test
    void shouldIncludeTempBetweenMinus10And0Fee() throws Exception {
        // Temp: -1°C, Wind: 5m/s, Fog
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.TALLINN,
                VehicleType.SCOOTER,
                LocalDateTime.of(2025, 3, 21, 12, 15)
        );

        // Base fee + Temp -10°C...0°C fee, 3.5 + 0.5
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFee").value(4.0));
    }

    @Test
    void shouldIncludeWindSpeedBetween10And20Fee() throws Exception {
        // Temp: 10°C, Wind: 18m/s, Strong Wind
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.TALLINN,
                VehicleType.BIKE,
                LocalDateTime.of(2025, 3, 21, 13, 15)
        );

        // Base fee + Wind 10m/s...20m/s, 3.0 + 0.5
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFee").value(3.5));
    }

    @Test
    void shouldIncludeRainPhenomenonFee() throws Exception {
        // Temp: 3°C, Wind: 12m/s, Light Rain
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.TALLINN,
                VehicleType.SCOOTER,
                LocalDateTime.of(2025, 3, 21, 14, 15)
        );

        // Base fee + Rain fee, 3.5 + 0.5
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFee").value(4.0));
    }

    @Test
    void shouldIncludeSnowSleetFee() throws Exception {
        // Temp: -9°C, Wind: 8m/s, Heavy Snow
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.PARNU,
                VehicleType.BIKE,
                LocalDateTime.of(2025, 3, 21, 11, 15)
        );

        // Base fee + Temp fee + Snow fee, 2.0 + 0.5 + 1.0
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFee").value(3.5));
    }

    @Test
    void shouldUseLatestWeather_whenTimestampNotProvided() throws Exception {
        // Latest weather data: Temp: 20°C, Wind: 15m/s, Strong Wind
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.PARNU,
                VehicleType.BIKE,
                null
        );

        // Base fee + Wind fee, 2.0 + 0.5
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFee").value(2.5));
    }

    @Test
    void shouldThrowForbiddenForExtremeWeather() throws Exception {
        // Temp: 2°C, Wind: 8m/s, Hail
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.TARTU,
                VehicleType.SCOOTER,
                LocalDateTime.of(2025, 3, 21, 13, 15) // Thunderstorm
        );

        // ForbiddenVehicleUsageException, Hail
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("extreme weather")));
    }

    @Test
    void shouldReturnNotFoundWhenVehicleTypeIsMissing() throws Exception {
        String invalidJson = """
                {
                    "city": "TALLINN"
                }
                """;

        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Vehicle type must not be null")));
    }

    @Test
    void shouldReturnNotFoundWhenCityIsMissing() throws Exception {
        String invalidJson = """
                {
                    "vehicleType": "BIKE"
                }
                """;

        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("City must not be null")));
    }

    @Test
    void shouldThrowInvalidCityEnum() throws Exception {
        String invalidJson = """
                {
                    "city": "NEWYORK",
                    "vehicleType": "CAR"
                }
                """;

        // InvalidCityException
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid city. Allowed values: TALLINN, TARTU, PARNU"));
    }

    @Test
    void shouldThrowInvalidVehicleEnum() throws Exception {
        String invalidJson = """
                {
                    "city": "TARTU",
                    "vehicleType": "PLANE"
                }
                """;

        // HttpMessageNotReadableException
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid vehicle type. Allowed values: CAR, SCOOTER, BIKE"));
    }

    @Test
    void shouldThrowWhenClosestWeatherIsTooFarInPast() throws Exception {
        DeliveryFeeRequestDto request = new DeliveryFeeRequestDto(
                City.PARNU,
                VehicleType.CAR,
                LocalDateTime.of(1990, 1, 1, 0, 0)
        );

        // ClosestWeatherMoreThanOneDayApartException
        mockMvc.perform(post("/api/delivery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("more than 24 hours apart")));
    }
}
