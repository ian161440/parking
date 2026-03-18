package com.pust.parking.mapper;

import com.pust.parking.entity.ViolationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ViolationRecordMapper {

    List<ViolationRecord> listAll();

    ViolationRecord findById(@Param("id") Long id);

    int insert(ViolationRecord record);

    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("handlerId") Long handlerId);
}
