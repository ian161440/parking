package com.pust.parking.controller;

import com.pust.parking.common.ApiResponse;
import com.pust.parking.entity.ExternalParkingLot;
import com.pust.parking.service.ExternalParkingLotService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external-parking-lots")
@CrossOrigin
public class ExternalParkingLotController {

    private final ExternalParkingLotService externalParkingLotService;

    public ExternalParkingLotController(ExternalParkingLotService externalParkingLotService) {
        this.externalParkingLotService = externalParkingLotService;
    }

    @GetMapping("/recommend")
    public ApiResponse<List<ExternalParkingLot>> getRecommendations() {
        return ApiResponse.success(externalParkingLotService.getRecommendations());
    }

    @GetMapping
    public ApiResponse<List<ExternalParkingLot>> listAll() {
        return ApiResponse.success(externalParkingLotService.listAll());
    }
}
