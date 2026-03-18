package com.pust.parking.mapper;

import com.pust.parking.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {

    SysUser findByUsername(@Param("username") String username);

    int insert(SysUser user);
}
