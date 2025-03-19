package com.fooddelivery.fooddeliveryfujitsu.dto;

import com.fooddelivery.fooddeliveryfujitsu.enums.City;
import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for calculating delivery fee")
public class DeliveryFeeRequestDto {

    @Schema(description = "City name", example = "Tallinn")
    @NotNull(message = "City must not be null")
    private City city;

    @Schema(description = "Vehicle type", example = "Bike")
    @NotNull(message = "Vehicle type must not be null")
    private VehicleType vehicleType;

    @Schema(description = "Optional timestamp for historical fee calculation", example = "2025-03-18T15:15:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime time;
}
