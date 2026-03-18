package com.pust.parking.service;

import com.pust.parking.entity.ExternalParkingLot;

import java.util.List;

public interface ExternalParkingLotService {

    /**
     * 获取推荐的外部停车场列表
     * 综合考虑距离、价格、空余车位等因素
     */
    List<ExternalParkingLot> getRecommendations();

    List<ExternalParkingLot> listAll();
}
