package com.fooddelivery.fooddeliveryfujitsu.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception e) {
        log.error("Internal server error", e);
        return new ResponseEntity<>(new ErrorResponse("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException e) {
        log.warn("{}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenVehicleUsageException.class)
    public ResponseEntity<Object> handleForbiddenVehicleUsageException(ForbiddenVehicleUsageException e) {
        log.warn("{}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WeatherDataFetchException.class)
    public ResponseEntity<Object> handleWeatherDataFetchException(WeatherDataFetchException e) {
        log.error("{}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InvalidCityException.class)
    public ResponseEntity<Object> handleInvalidCityException(InvalidCityException e) {
        log.warn("{}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidVehicleTypeException.class)
    public ResponseEntity<Object> handleInvalidVehicleTypeException(InvalidVehicleTypeException e) {
        log.warn("{}", e.getMessage());
        return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", errorMessage);
        return new ResponseEntity<>(new ErrorResponse(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Invalid request format: {}", e.getMessage());

        if (e.getMessage().contains("City")) {
            return new ResponseEntity<>(new ErrorResponse("Invalid city. Allowed values: TALLINN, TARTU, PARNU"), HttpStatus.BAD_REQUEST);
        }

        if (e.getMessage().contains("VehicleType")) {
            return new ResponseEntity<>(new ErrorResponse("Invalid vehicle type. Allowed values: CAR, SCOOTER, BIKE"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new ErrorResponse("Invalid request format"), HttpStatus.BAD_REQUEST);
    }
}
