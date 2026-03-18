package com.pust.parking.service.impl;

import com.pust.parking.entity.ViolationRecord;
import com.pust.parking.mapper.ViolationRecordMapper;
import com.pust.parking.service.ViolationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ViolationServiceImpl implements ViolationService {

    private final ViolationRecordMapper violationRecordMapper;

    public ViolationServiceImpl(ViolationRecordMapper violationRecordMapper) {
        this.violationRecordMapper = violationRecordMapper;
    }

    @Override
    public List<ViolationRecord> listAll() {
        return violationRecordMapper.listAll();
    }

    @Override
    @Transactional
    public ViolationRecord create(ViolationRecord record) {
        if (record.getDescription() == null || record.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("违规描述不能为空");
        }
        if (record.getStatus() == null || record.getStatus().trim().isEmpty()) {
            record.setStatus("UNPROCESSED");
        }
        violationRecordMapper.insert(record);
        return violationRecordMapper.findById(record.getId());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status, Long handlerId) {
        ViolationRecord existing = violationRecordMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("违规记录不存在");
        }
        violationRecordMapper.updateStatus(id, status, handlerId);
    }
}
