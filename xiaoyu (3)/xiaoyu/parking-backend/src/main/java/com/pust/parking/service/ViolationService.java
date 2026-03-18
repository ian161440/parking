package com.pust.parking.service;

import com.pust.parking.entity.ViolationRecord;

import java.util.List;

public interface ViolationService {

    List<ViolationRecord> listAll();

    ViolationRecord create(ViolationRecord record);

    void updateStatus(Long id, String status, Long handlerId);
}
