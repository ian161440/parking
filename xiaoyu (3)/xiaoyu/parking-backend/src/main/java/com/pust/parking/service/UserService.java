package com.pust.parking.service;

import com.pust.parking.dto.LoginRequest;
import com.pust.parking.dto.RegisterRequest;
import com.pust.parking.entity.SysUser;

public interface UserService {

    SysUser register(RegisterRequest request);

    SysUser login(LoginRequest request);
}
