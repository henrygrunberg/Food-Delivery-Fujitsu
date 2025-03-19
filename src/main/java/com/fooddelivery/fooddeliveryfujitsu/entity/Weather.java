package com.fooddelivery.fooddeliveryfujitsu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "weather", uniqueConstraints = @UniqueConstraint(columnNames = {"station_id", "timestamp"}))
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long weatherId;

    @ManyToOne
    @JoinColumn(name = "station_id", referencedColumnName = "station_id", nullable = false)
    private Station station;

    @Column(name = "wmo_code", length = 50, nullable = false)
    private String wmoCode;

    @Column(name = "air_temperature", nullable = false)
    private Double airTemperature;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(name = "weather_phenomenon", nullable = false)
    private String weatherPhenomenon;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
