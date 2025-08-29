package com.graduationprojectordermanagementsystem.controller.admin;

import com.graduationprojectordermanagementsystem.service.UserService;
import com.graduationprojectordermanagementsystem.util.JwtUtils;
import com.graduationprojectordermanagementsystem.util.RedisUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户接口")
@RestController("adminUserController")
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private RedisUtils redisUtils;


}
