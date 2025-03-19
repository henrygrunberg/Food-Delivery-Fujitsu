package com.fooddelivery.fooddeliveryfujitsu.controller;

import com.fooddelivery.fooddeliveryfujitsu.dto.StationFeeDto;
import com.fooddelivery.fooddeliveryfujitsu.dto.StationFeeUpdateDto;
import com.fooddelivery.fooddeliveryfujitsu.service.StationFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/station-fees")
@RequiredArgsConstructor
@Tag(name = "Station Fees", description = "API for managing station fees")
public class StationFeeController {

    private final StationFeeService stationFeeService;

    @GetMapping
    @Operation(summary = "Fetch all station fees", description = "Retrieves all the station fees available.")
    @ApiResponse(responseCode = "200", description = "Station fees fetched successfully")
    @ApiResponse(responseCode = "404", description = "Station fees not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<List<StationFeeDto>> getAllStationFees() {
        return ResponseEntity.ok(stationFeeService.getAllStationFees());
    }

    @GetMapping("/{city}")
    @Operation(summary = "Fetch station fees by city", description = "Retrieves the fees for a specific station.")
    @ApiResponse(responseCode = "200", description = "Station fees fetched successfully")
    @ApiResponse(responseCode = "400", description = "Invalid city provided")
    @ApiResponse(responseCode = "404", description = "Station fees not found for the given city")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<List<StationFeeDto>> getStationFeesByCity(@PathVariable String city) {
        return ResponseEntity.ok(stationFeeService.getStationFeesByCity(city));
    }

    @GetMapping("/{city}/{vehicleType}")
    @Operation(summary = "Fetch station fees by station name and vehicle type", description = "Retrieves the fees for a specific station and vehicle type.")
    @ApiResponse(responseCode = "200", description = "Station fees fetched successfully")
    @ApiResponse(responseCode = "400", description = "Invalid city or vehicle type provided")
    @ApiResponse(responseCode = "404", description = "Station fee not found for the given city and vehicle type")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<StationFeeDto> getStationFeesByCityAndVehicleType(@PathVariable String city, @PathVariable String vehicleType) {
        return ResponseEntity.ok(stationFeeService.getStationFeesByCityAndVehicleType(city, vehicleType));
    }

    @PutMapping
    @Operation(summary = "Update station fee", description = "Updates the fee settings for a specific station and vehicle type.")
    @ApiResponse(responseCode = "200", description = "Station fee updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "404", description = "Station fee not found for the given station and vehicle type")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<StationFeeDto> updateStationFee(@Valid @RequestBody StationFeeUpdateDto updateDto) {
        return ResponseEntity.ok(stationFeeService.updateStationFee(updateDto));
    }
}
