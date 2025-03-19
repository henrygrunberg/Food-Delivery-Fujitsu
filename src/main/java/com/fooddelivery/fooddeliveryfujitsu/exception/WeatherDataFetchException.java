package com.fooddelivery.fooddeliveryfujitsu.exception;

public class WeatherDataFetchException extends RuntimeException {
    public WeatherDataFetchException(String message, Throwable cause) {
        super(message, cause);
    }
    public WeatherDataFetchException(String message) { super(message); }
}
