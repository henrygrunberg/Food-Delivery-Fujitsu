package com.fooddelivery.fooddeliveryfujitsu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FoodDeliveryFujitsuApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryFujitsuApplication.class, args);
    }

}
