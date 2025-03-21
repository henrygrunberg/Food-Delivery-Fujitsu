package com.fooddelivery.fooddeliveryfujitsu.dto;

import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for updating station fee settings")
public class StationFeeUpdateDto {

    @Schema(description = "Station name (linked to Weather)", example = "Tallinn-Harku")
    @NotBlank(message = "Station name must not be empty")
    private String stationName;

    @Schema(description = "Vehicle type", example = "BIKE")
    @NotNull(message = "Vehicle type must not be null")
    private VehicleType vehicleType;

    @Schema(description = "Base fee (0 - 10€)", example = "3.00")
    @DecimalMin(value = "0.00", message = "Base fee must be at least 0.00€")
    @DecimalMax(value = "10.00", message = "Base fee cannot be more than 10.00€")
    private Double baseFee;

    @Schema(description = "Extra fee for temperatures below -10°C (0 - 5€)", example = "1.00")
    @DecimalMin(value = "0.00", message = "Temperature below -10°C fee must be at least 0.00€")
    @DecimalMax(value = "5.00", message = "Temperature below -10°C fee cannot be more than 5.00€")
    private Double temperatureBelowMinus10Fee;

    @Schema(description = "Extra fee for temperatures between -10°C and 0°C (0 - 5€)", example = "0.50")
    @DecimalMin(value = "0.00", message = "Temperature between -10°C and 0°C fee must be at least 0.00€")
    @DecimalMax(value = "5.00", message = "Temperature between -10°C and 0°C fee cannot be more than 5.00€")
    private Double temperatureBetweenMinus10And0Fee;

    @Schema(description = "Extra fee for wind speeds between 10m/s and 20m/s (0 - 5€)", example = "0.50")
    @DecimalMin(value = "0.00", message = "Wind speed fee must be at least 0.00€")
    @DecimalMax(value = "5.00", message = "Wind speed fee cannot be more than 5.00€")
    private Double windSpeedBetween10And20Fee;

    @Schema(description = "Extra fee for snow or sleet (0 - 5€)", example = "1.00")
    @DecimalMin(value = "0.00", message = "Snow/sleet fee must be at least 0.00€")
    @DecimalMax(value = "5.00", message = "Snow/sleet fee cannot be more than 5.00€")
    private Double weatherPhenomenonSnowSleetFee;

    @Schema(description = "Extra fee for rain (0 - 5€)", example = "0.50")
    @DecimalMin(value = "0.00", message = "Rain fee must be at least 0.00€")
    @DecimalMax(value = "5.00", message = "Rain fee cannot be more than 5.00€")
    private Double weatherPhenomenonRainFee;
}
