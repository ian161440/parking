package com.pust.parking.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViolationRecord {

    private Long id;

    private Long userId;

    private Long vehicleId;

    private Long spaceId;

    private String description;

    private String status;

    private LocalDateTime createTime;

    private Long handlerId;
}
