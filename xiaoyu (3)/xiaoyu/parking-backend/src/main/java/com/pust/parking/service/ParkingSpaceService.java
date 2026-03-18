package com.pust.parking.service;

import com.pust.parking.entity.ParkingSpace;

import java.util.List;

public interface ParkingSpaceService {

    List<ParkingSpace> listAll();

    List<ParkingSpace> listAvailable();

    /**
     * 简单规则的车位推荐：目前先基于可用车位列表做排序/截取，后续可接入大数据模块。
     */
    List<ParkingSpace> recommendSpacesForUser(Long userId);

    ParkingSpace create(ParkingSpace space);

    ParkingSpace update(ParkingSpace space);

    void delete(Long id);
}
