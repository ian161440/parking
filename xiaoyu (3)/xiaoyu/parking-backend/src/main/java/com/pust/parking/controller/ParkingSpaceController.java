package com.pust.parking.controller;

import com.pust.parking.common.ApiResponse;
import com.pust.parking.entity.ParkingSpace;
import com.pust.parking.service.ParkingSpaceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@CrossOrigin
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    public ParkingSpaceController(ParkingSpaceService parkingSpaceService) {
        this.parkingSpaceService = parkingSpaceService;
    }

    @GetMapping("/available")
    public ApiResponse<List<ParkingSpace>> listAvailable() {
        return ApiResponse.success(parkingSpaceService.listAvailable());
    }

    @GetMapping("/recommend")
    public ApiResponse<List<ParkingSpace>> recommend(@RequestParam("userId") Long userId) {
        return ApiResponse.success(parkingSpaceService.recommendSpacesForUser(userId));
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<ParkingSpace>> listAll() {
        return ApiResponse.success(parkingSpaceService.listAll());
    }

    @PostMapping("/admin")
    public ApiResponse<ParkingSpace> create(@RequestBody ParkingSpace space) {
        return ApiResponse.success(parkingSpaceService.create(space));
    }

    @PutMapping("/admin/{id}")
    public ApiResponse<ParkingSpace> update(@PathVariable("id") Long id, @RequestBody ParkingSpace space) {
        space.setId(id);
        return ApiResponse.success(parkingSpaceService.update(space));
    }

    @DeleteMapping("/admin/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        parkingSpaceService.delete(id);
        return ApiResponse.success(null);
    }
}
