package com.pust.parking.service;

import com.pust.parking.entity.Reservation;

import java.util.List;

public interface ReservationService {

    Reservation create(Reservation reservation);

    List<Reservation> listByUserId(Long userId);

    List<Reservation> listAll();

    void updateStatus(Long id, String status);
}
