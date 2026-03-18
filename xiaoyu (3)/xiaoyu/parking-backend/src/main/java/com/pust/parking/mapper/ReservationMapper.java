package com.pust.parking.mapper;

import com.pust.parking.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper {

    int insert(Reservation reservation);

    List<Reservation> listByUserId(@Param("userId") Long userId);

    List<Reservation> listAll();

    List<Reservation> findConflicts(@Param("spaceId") Long spaceId,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
