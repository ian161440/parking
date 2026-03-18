package com.pust.parking.controller;

import com.pust.parking.common.ApiResponse;
import com.pust.parking.dto.LoginRequest;
import com.pust.parking.dto.RegisterRequest;
import com.pust.parking.entity.SysUser;
import com.pust.parking.service.UserService;
import com.pust.parking.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Validated
@CrossOrigin
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ApiResponse<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        SysUser user = userService.register(request);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return ApiResponse.success("注册成功", vo);
    }

    @PostMapping("/login")
    public ApiResponse<UserVO> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = userService.login(request);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return ApiResponse.success("登录成功", vo);
    }
}
