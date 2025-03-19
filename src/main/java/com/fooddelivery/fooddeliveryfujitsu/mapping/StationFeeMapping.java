package com.fooddelivery.fooddeliveryfujitsu.mapping;

import com.fooddelivery.fooddeliveryfujitsu.dto.StationFeeDto;
import com.fooddelivery.fooddeliveryfujitsu.dto.StationFeeUpdateDto;
import com.fooddelivery.fooddeliveryfujitsu.entity.Station;
import com.fooddelivery.fooddeliveryfujitsu.entity.StationFee;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StationFeeMapping {

    @Mapping(target = "station", expression = "java(station)")
    StationFee dtoToEntity(StationFeeDto dto, @Context Station station);

    @Mapping(source = "station.stationName", target = "stationName")
    StationFeeDto entityToDto(StationFee stationFee);

    List<StationFeeDto> entitysToDtos(List<StationFee> stationFees);
    List<StationFee> dtosToEntitys(List<StationFeeDto> stationFeesDto);

    @Mapping(target = "changedAt", ignore = true)
    @Mapping(target = "station", expression = "java(station)")
    StationFee updateDtoToEntity(StationFeeUpdateDto dto, @Context Station station);

    @Mapping(source = "station.stationName", target = "stationName")
    StationFeeUpdateDto entityToUpdateDto(StationFee stationFee);
}
