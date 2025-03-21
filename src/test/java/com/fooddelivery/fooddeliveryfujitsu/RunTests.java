package com.fooddelivery.fooddeliveryfujitsu;

import com.fooddelivery.fooddeliveryfujitsu.controller.DeliveryFeeControllerTest;
import com.fooddelivery.fooddeliveryfujitsu.controller.StationFeeControllerTest;
import com.fooddelivery.fooddeliveryfujitsu.service.DeliveryFeeServiceTest;
import com.fooddelivery.fooddeliveryfujitsu.service.StationFeeServiceTest;
import com.fooddelivery.fooddeliveryfujitsu.service.StationServiceTest;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        // Unit tests
        DeliveryFeeServiceTest.class,
        StationFeeServiceTest.class,
        StationServiceTest.class,
        // Integration tests
        DeliveryFeeControllerTest.class,
        StationFeeControllerTest.class
})
@TestClassOrder(OrderAnnotation.class)
public class RunTests {
}
