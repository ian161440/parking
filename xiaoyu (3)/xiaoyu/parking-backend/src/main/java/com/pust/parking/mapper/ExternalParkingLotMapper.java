package com.pust.parking.mapper;

import com.pust.parking.entity.ExternalParkingLot;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExternalParkingLotMapper {

    List<ExternalParkingLot> listAll();

    ExternalParkingLot findById(Long id);
}
