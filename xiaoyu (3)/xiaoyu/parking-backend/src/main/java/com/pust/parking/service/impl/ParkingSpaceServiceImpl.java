package com.pust.parking.service.impl;

import com.pust.parking.entity.ParkingSpace;
import com.pust.parking.mapper.ParkingSpaceMapper;
import com.pust.parking.service.ParkingSpaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingSpaceServiceImpl implements ParkingSpaceService {

    private final ParkingSpaceMapper parkingSpaceMapper;

    public ParkingSpaceServiceImpl(ParkingSpaceMapper parkingSpaceMapper) {
        this.parkingSpaceMapper = parkingSpaceMapper;
    }

    @Override
    public List<ParkingSpace> listAll() {
        return parkingSpaceMapper.listAll();
    }

    @Override
    public List<ParkingSpace> listAvailable() {
        return parkingSpaceMapper.listAvailable();
    }

    @Override
    public List<ParkingSpace> recommendSpacesForUser(Long userId) {
        // TODO: 这里可以接入基于用户偏好、历史预约记录的大数据推荐逻辑
        // 规则推荐示例：
        // 1. 仅在可用车位中推荐（FREE）；
        // 2. 按楼层从低到高排序；
        // 3. 同一楼层内按车位编号从小到大排序；
        // 4. 仅返回前若干个车位（例如前 20 个）。

        List<ParkingSpace> allAvailable = parkingSpaceMapper.listAvailable();

        Comparator<ParkingSpace> comparator = Comparator
                // 楼层：尽量按数字排序，无法解析为数字的排在后面
                .comparingInt((ParkingSpace space) -> {
                    String floor = space.getFloor();
                    try {
                        return floor == null ? Integer.MAX_VALUE : Integer.parseInt(floor.trim());
                    } catch (NumberFormatException e) {
                        return Integer.MAX_VALUE;
                    }
                })
                // 车位编号：按字符串升序
                .thenComparing((ParkingSpace space) -> space.getSpaceCode() == null ? "" : space.getSpaceCode());

        // 截取前 20 个作为推荐结果，防止列表过长
        return allAvailable.stream()
                .sorted(comparator)
                .limit(20)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParkingSpace create(ParkingSpace space) {
        if (space.getSpaceCode() == null || space.getSpaceCode().trim().isEmpty()) {
            throw new IllegalArgumentException("车位编号不能为空");
        }
        if (space.getStatus() == null || space.getStatus().trim().isEmpty()) {
            space.setStatus("FREE");
        }
        parkingSpaceMapper.insert(space);
        return parkingSpaceMapper.findById(space.getId());
    }

    @Override
    @Transactional
    public ParkingSpace update(ParkingSpace space) {
        if (space.getId() == null) {
            throw new IllegalArgumentException("车位ID不能为空");
        }
        ParkingSpace existing = parkingSpaceMapper.findById(space.getId());
        if (existing == null) {
            throw new IllegalArgumentException("车位不存在");
        }
        parkingSpaceMapper.update(space);
        return parkingSpaceMapper.findById(space.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ParkingSpace existing = parkingSpaceMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("车位不存在");
        }
        parkingSpaceMapper.deleteById(id);
    }
}
