package com.pust.parking.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reservation {

    private Long id;

    private Long userId;

    private Long vehicleId;

    private Long spaceId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;

    private LocalDateTime createTime;
}
