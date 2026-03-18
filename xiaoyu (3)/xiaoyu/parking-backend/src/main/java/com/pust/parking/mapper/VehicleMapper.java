package com.pust.parking.mapper;

import com.pust.parking.entity.Vehicle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VehicleMapper {

    List<Vehicle> listByUserId(@Param("userId") Long userId);

    List<Vehicle> listAll();

    Vehicle findById(@Param("id") Long id);

    int insert(Vehicle vehicle);

    int update(Vehicle vehicle);

    int deleteById(@Param("id") Long id);
}
