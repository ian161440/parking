package com.pust.parking.controller;

import com.pust.parking.common.ApiResponse;
import com.pust.parking.entity.Reservation;
import com.pust.parking.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ApiResponse<Reservation> create(@RequestBody Reservation reservation) {
        return ApiResponse.success(reservationService.create(reservation));
    }

    @GetMapping("/my")
    public ApiResponse<List<Reservation>> listMy(@RequestParam("userId") Long userId) {
        return ApiResponse.success(reservationService.listByUserId(userId));
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<Reservation>> listAll() {
        return ApiResponse.success(reservationService.listAll());
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        reservationService.updateStatus(id, status);
        return ApiResponse.success(null);
    }
}
