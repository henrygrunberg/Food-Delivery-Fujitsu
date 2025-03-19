package com.fooddelivery.fooddeliveryfujitsu.entity;

import com.fooddelivery.fooddeliveryfujitsu.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "station_fee", uniqueConstraints = @UniqueConstraint(columnNames = {"station_id", "vehicle_type"}))
public class StationFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationFeeId;

    @ManyToOne
    @JoinColumn(name = "station_id", referencedColumnName = "station_id", nullable = false)
    private Station station;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "base_fee", nullable = false)
    private Double baseFee;

    @Column(name = "temperature_below_minus_10_fee", nullable = false)
    private Double temperatureBelowMinus10Fee;

    @Column(name = "temperature_between_minus_10_and_0_fee", nullable = false)
    private Double temperatureBetweenMinus10And0Fee;

    @Column(name = "wind_speed_between_10_and_20_fee", nullable = false)
    private Double windSpeedBetween10And20Fee;

    @Column(name = "weather_phenomenon_snow_sleet_fee", nullable = false)
    private Double weatherPhenomenonSnowSleetFee;

    @Column(name = "weather_phenomenon_rain_fee", nullable = false)
    private Double weatherPhenomenonRainFee;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.changedAt = LocalDateTime.now();
    }
}
