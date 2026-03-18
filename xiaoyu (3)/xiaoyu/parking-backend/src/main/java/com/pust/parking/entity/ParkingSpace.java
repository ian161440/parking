package com.pust.parking.entity;

import lombok.Data;

@Data
public class ParkingSpace {

    private Long id;

    private String spaceCode;

    private String floor;

    private String type;

    private String status;

    private String remark;
}
