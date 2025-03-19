package com.fooddelivery.fooddeliveryfujitsu.repository;

import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.entity.StationFee;
import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationFeeRepository extends JpaRepository<StationFee, Long> {

    Optional<StationFee> findByStationAndVehicleType(Station station, VehicleType vehicleType);
}
