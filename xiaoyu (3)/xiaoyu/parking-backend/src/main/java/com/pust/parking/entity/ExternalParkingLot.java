package com.pust.parking.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExternalParkingLot {

    private Long id;

    private String name;

    private String address;

    private String phone;

    private BigDecimal distanceKm;

    private Integer availableSpaces;

    private BigDecimal pricePerHour;

    private String parkingType;

    private String businessHours;

    private BigDecimal score;
}
