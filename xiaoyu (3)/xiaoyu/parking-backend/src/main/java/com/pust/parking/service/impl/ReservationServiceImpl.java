package com.pust.parking.service.impl;

import com.pust.parking.entity.ParkingSpace;
import com.pust.parking.entity.Reservation;
import com.pust.parking.entity.Vehicle;
import com.pust.parking.mapper.ParkingSpaceMapper;
import com.pust.parking.mapper.ReservationMapper;
import com.pust.parking.mapper.VehicleMapper;
import com.pust.parking.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final VehicleMapper vehicleMapper;

    public ReservationServiceImpl(ReservationMapper reservationMapper,
                                  ParkingSpaceMapper parkingSpaceMapper,
                                  VehicleMapper vehicleMapper) {
        this.reservationMapper = reservationMapper;
        this.parkingSpaceMapper = parkingSpaceMapper;
        this.vehicleMapper = vehicleMapper;
    }

    @Override
    @Transactional
    public Reservation create(Reservation reservation) {
        if (reservation.getUserId() == null || reservation.getVehicleId() == null || reservation.getSpaceId() == null) {
            throw new IllegalArgumentException("用户、车辆和车位信息不能为空");
        }
        if (reservation.getStartTime() == null || reservation.getEndTime() == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        LocalDateTime now = LocalDateTime.now();
        if (reservation.getEndTime().isBefore(reservation.getStartTime())) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        if (reservation.getEndTime().isBefore(now)) {
            throw new IllegalArgumentException("结束时间不能早于当前时间");
        }

        ParkingSpace space = parkingSpaceMapper.findById(reservation.getSpaceId());
        if (space == null) {
            throw new IllegalArgumentException("车位不存在");
        }

        Vehicle vehicle = vehicleMapper.findById(reservation.getVehicleId());
        if (vehicle == null) {
            throw new IllegalArgumentException("车辆不存在");
        }

        List<Reservation> conflicts = reservationMapper.findConflicts(
                reservation.getSpaceId(), reservation.getStartTime(), reservation.getEndTime());
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("该时间段车位已被预约");
        }

        if (reservation.getStatus() == null || reservation.getStatus().trim().isEmpty()) {
            reservation.setStatus("PENDING");
        }

        reservationMapper.insert(reservation);
        return reservation;
    }

    @Override
    public List<Reservation> listByUserId(Long userId) {
        return reservationMapper.listByUserId(userId);
    }

    @Override
    public List<Reservation> listAll() {
        return reservationMapper.listAll();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        reservationMapper.updateStatus(id, status);
    }
}
