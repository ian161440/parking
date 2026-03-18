package com.pust.parking.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Vehicle {

    private Long id;

    private Long userId;

    private String plateNumber;

    private String vehicleType;

    private Boolean isSpecial;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
