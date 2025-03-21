package com.fooddelivery.fooddeliveryfujitsu.controller;

import com.fooddelivery.fooddeliveryfujitsu.dto.DeliveryFeeRequestDto;
import com.fooddelivery.fooddeliveryfujitsu.dto.DeliveryFeeResponseDto;
import com.fooddelivery.fooddeliveryfujitsu.service.DeliveryFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;


@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery Fee", description = "API to calculate delivery fees")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;


    @PostMapping()
    @Operation(summary = "Calculate delivery fee", description = "Calculates delivery fee based on city, vehicle type, and weather conditions.")
    @ApiResponse(responseCode = "200", description = "Delivery fee calculated successfully")
    @ApiResponse(responseCode = "400", description = "Usage of selected vehicle type is forbidden or invalid input, Optional weather date more than 24 hours apart from the latest weather data.")
    @ApiResponse(responseCode = "404", description = "City or vehicle type not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<DeliveryFeeResponseDto> calculateDeliveryFee(@Valid @RequestBody DeliveryFeeRequestDto request) {
        Optional<LocalDateTime> time = Optional.ofNullable(request.getTime());
        double fee = deliveryFeeService.calculateDeliveryFee(request, time);
        return ResponseEntity.ok(new DeliveryFeeResponseDto(fee));
    }
}
