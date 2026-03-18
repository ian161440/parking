package com.pust.parking.service.impl;

import com.pust.parking.entity.Vehicle;
import com.pust.parking.mapper.VehicleMapper;
import com.pust.parking.service.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;

    public VehicleServiceImpl(VehicleMapper vehicleMapper) {
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    public List<Vehicle> listByUserId(Long userId) {
        return vehicleMapper.listByUserId(userId);
    }

    @Override
    public List<Vehicle> listAll() {
        return vehicleMapper.listAll();
    }

    @Override
    @Transactional
    public Vehicle create(Vehicle vehicle) {
        if (vehicle.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (vehicle.getPlateNumber() == null || vehicle.getPlateNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("车牌号不能为空");
        }
        vehicleMapper.insert(vehicle);
        return vehicleMapper.findById(vehicle.getId());
    }

    @Override
    @Transactional
    public Vehicle update(Vehicle vehicle) {
        if (vehicle.getId() == null) {
            throw new IllegalArgumentException("车辆ID不能为空");
        }
        Vehicle existing = vehicleMapper.findById(vehicle.getId());
        if (existing == null) {
            throw new IllegalArgumentException("车辆不存在");
        }
        vehicleMapper.update(vehicle);
        return vehicleMapper.findById(vehicle.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vehicle existing = vehicleMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("车辆不存在");
        }
        vehicleMapper.deleteById(id);
    }
}
