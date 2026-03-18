package com.pust.parking.mapper;

import com.pust.parking.entity.ParkingSpace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkingSpaceMapper {

    List<ParkingSpace> listAll();

    List<ParkingSpace> listAvailable();

    ParkingSpace findById(@Param("id") Long id);

    int insert(ParkingSpace space);

    int update(ParkingSpace space);

    int deleteById(@Param("id") Long id);
}
