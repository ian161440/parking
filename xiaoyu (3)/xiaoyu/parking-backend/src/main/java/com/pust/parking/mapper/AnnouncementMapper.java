package com.pust.parking.mapper;

import com.pust.parking.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    List<Announcement> listAllOrderByTop();

    Announcement findById(@Param("id") Long id);

    int insert(Announcement announcement);

    int update(Announcement announcement);

    int deleteById(@Param("id") Long id);
}
