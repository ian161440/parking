package com.pust.parking.entity;

import lombok.Data;

@Data
public class UserPreference {

    private Long id;

    private Long userId;

    private String preferType;

    private String preferFloor;

    private String preferTimePeriod;

    private Integer rating;
}
