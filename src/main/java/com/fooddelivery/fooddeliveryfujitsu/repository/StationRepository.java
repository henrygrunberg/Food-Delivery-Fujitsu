package com.fooddelivery.fooddeliveryfujitsu.repository;

import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    Optional<Station> findByStationName(String stationName);
}
