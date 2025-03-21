package com.fooddelivery.fooddeliveryfujitsu.exception;

public class ClosestWeatherMoreThanOneDayApartException extends RuntimeException {
    public ClosestWeatherMoreThanOneDayApartException(String message) {
        super(message);
    }
}