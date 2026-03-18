package com.pust.parking.controller;

import com.pust.parking.common.ApiResponse;
import com.pust.parking.entity.Vehicle;
import com.pust.parking.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/my")
    public ApiResponse<List<Vehicle>> listMy(@RequestParam("userId") Long userId) {
        return ApiResponse.success(vehicleService.listByUserId(userId));
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<Vehicle>> listAll() {
        return ApiResponse.success(vehicleService.listAll());
    }

    @PostMapping
    public ApiResponse<Vehicle> create(@RequestBody Vehicle vehicle) {
        return ApiResponse.success(vehicleService.create(vehicle));
    }

    @PutMapping("/{id}")
    public ApiResponse<Vehicle> update(@PathVariable("id") Long id, @RequestBody Vehicle vehicle) {
        vehicle.setId(id);
        return ApiResponse.success(vehicleService.update(vehicle));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        vehicleService.delete(id);
        return ApiResponse.success(null);
    }
}
