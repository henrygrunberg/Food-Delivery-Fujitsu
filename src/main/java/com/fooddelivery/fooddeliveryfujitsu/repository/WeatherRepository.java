package com.fooddelivery.fooddeliveryfujitsu.repository;

import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    Optional<Weather> findTopByStationOrderByTimestampDesc(Station station);

    @Query("SELECT w FROM Weather w WHERE w.station = :station ORDER BY w.timestamp DESC")
    List<Weather> findAllWeatherForStation(Station station);
}
