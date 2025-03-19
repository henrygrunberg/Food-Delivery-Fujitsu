package com.fooddelivery.fooddeliveryfujitsu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for delivery fee response")
public class DeliveryFeeResponseDto {

    @Schema(description = "Total delivery fee in EUR", example = "4.5")
    private Double totalFee;
}
