package com.pust.parking.service.impl;

import com.pust.parking.entity.ExternalParkingLot;
import com.pust.parking.mapper.ExternalParkingLotMapper;
import com.pust.parking.service.ExternalParkingLotService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExternalParkingLotServiceImpl implements ExternalParkingLotService {

    private final ExternalParkingLotMapper externalParkingLotMapper;

    public ExternalParkingLotServiceImpl(ExternalParkingLotMapper externalParkingLotMapper) {
        this.externalParkingLotMapper = externalParkingLotMapper;
    }

    @Override
    public List<ExternalParkingLot> getRecommendations() {
        List<ExternalParkingLot> allLots = externalParkingLotMapper.listAll();
        
        // 为每个停车场计算综合评分
        for (ExternalParkingLot lot : allLots) {
            BigDecimal score = calculateScore(lot);
            lot.setScore(score);
        }
        
        // 按评分降序排序
        return allLots.stream()
                .sorted(Comparator.comparing(ExternalParkingLot::getScore).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ExternalParkingLot> listAll() {
        return externalParkingLotMapper.listAll();
    }

    /**
     * 计算停车场综合评分
     * 考虑因素：距离（40%）、价格（30%）、空余车位比例（30%）
     */
    private BigDecimal calculateScore(ExternalParkingLot lot) {
        // 距离评分：距离越近分数越高（假设最远1000米）
        double distanceScore = Math.max(0, 100 - (lot.getDistanceKm().doubleValue() - 100) / 900.0 * 100);
        
        // 价格评分：价格越低分数越高（假设价格范围5-10元）
        double priceScore = Math.max(0, 100 - (lot.getPricePerHour().doubleValue() - 5) / 5.0 * 100);
        
        // 空余率评分：空余车位越多分数越高（假设总车位数存在parkingType字段中，这里简化处理）
        double availabilityScore = lot.getAvailableSpaces() != null ? 
                Math.min(100, lot.getAvailableSpaces() * 0.2) : 50;
        
        // 加权总分
        double totalScore = distanceScore * 0.4 + priceScore * 0.3 + availabilityScore * 0.3;
        
        return BigDecimal.valueOf(Math.round(totalScore * 100.0) / 100.0);
    }
}
