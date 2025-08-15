package com.graduationprojectordermanagementsystem.controller.user;

import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.pojo.vo.LoginVO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.UserService;
import com.graduationprojectordermanagementsystem.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private JwtUtils jwtUtils;


    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录:{}", loginDTO);

        User user = userService.login(loginDTO);

        //登录成功后，生成jwt令牌
        String token = jwtUtils.generateToken(loginDTO);

        LoginVO loginVO = new LoginVO(user.getId(), token);

        return Result.success(loginVO);
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        log.info("用户注册:{}", registerDTO);

        return Result.success(userService.register(registerDTO));
    }
}
