package com.pust.parking.service;

import com.pust.parking.entity.Vehicle;

import java.util.List;

public interface VehicleService {

    List<Vehicle> listByUserId(Long userId);

    List<Vehicle> listAll();

    Vehicle create(Vehicle vehicle);

    Vehicle update(Vehicle vehicle);

    void delete(Long id);
}
