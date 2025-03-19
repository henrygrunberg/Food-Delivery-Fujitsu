package com.fooddelivery.fooddeliveryfujitsu.dto;

import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for Station Fee information")
public class StationFeeDto {

    @Schema(description = "Station Fee ID", example = "1")
    private Long stationFeeId;

    @Schema(description = "Station name (linked to Weather)", example = "Tallinn-Harku")
    private String stationName;

    @Schema(description = "Vehicle type", example = "BIKE")
    private VehicleType vehicleType;

    @Schema(description = "Base fee (0 - 10€)", example = "3.00")
    private Double baseFee;

    @Schema(description = "Extra fee for temperatures below -10°C (0 - 5€)", example = "1.00")
    private Double temperatureBelowMinus10Fee;

    @Schema(description = "Extra fee for temperatures between -10°C and 0°C (0 - 5€)", example = "0.50")
    private Double temperatureBetweenMinus10And0Fee;

    @Schema(description = "Extra fee for wind speeds between 10m/s and 20m/s (0 - 5€)", example = "0.50")
    private Double windSpeedBetween10And20Fee;

    @Schema(description = "Extra fee for snow or sleet (0 - 5€)", example = "1.00")
    private Double weatherPhenomenonSnowSleetFee;

    @Schema(description = "Extra fee for rain (0 - 5€)", example = "0.50")
    private Double weatherPhenomenonRainFee;

    @Schema(description = "Timestamp of the last modification", example = "2025-03-18T15:30:00")
    private LocalDateTime changedAt;
}
