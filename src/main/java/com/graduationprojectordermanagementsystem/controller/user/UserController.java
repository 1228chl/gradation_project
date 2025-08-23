package com.graduationprojectordermanagementsystem.controller.user;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.pojo.vo.LoginVO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.UserService;
import com.graduationprojectordermanagementsystem.util.JwtUtils;
import com.graduationprojectordermanagementsystem.util.RedisUtils;
import com.graduationprojectordermanagementsystem.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
    @Resource
    private RedisUtils redisUtils;


    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("用户登录:{}", loginDTO);

        User user = userService.login(loginDTO);

        // 获取旧 Token
        String oldToken = redisUtils.getCache(user.getUsername(), String.class);
        if (oldToken != null) {
            String oldJti = jwtUtils.getJtiFromToken(oldToken);
            if (oldJti != null) {
                long expire = jwtUtils.getRemainingExpireSeconds(oldToken);
                redisUtils.addToBlacklist(oldJti, expire);
            }
        }
        // 再生成新 Token
        String newToken = jwtUtils.generateToken(user);
        redisUtils.setCache(user.getUsername(), newToken);

//        //登录成功后，生成jwt令牌
//        String token = jwtUtils.generateToken(user);

        //缓存jwt令牌
        redisUtils.setCache(user.getUsername(), newToken);

        return Result.success(new LoginVO(user.getId(), newToken));
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册:{}", registerDTO);

        return Result.success(userService.register(registerDTO));
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @RequireAnyRole({"user","admin"})
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String username = UserContext.getUsername();
        log.info("用户登出: {}", username);

        // 1. 如果没有 Token，也算登出成功
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.success();
        }

        String token = authHeader.substring(7);
        if (token.isEmpty()) {
            return Result.success();
        }

        // 2. 解析 jti 并加入黑名单
        try {
            String jti = jwtUtils.getJtiFromToken(token);
            if (jti != null) {
                long remainSeconds = jwtUtils.getRemainingExpireSeconds(token);
                redisUtils.addToBlacklist(jti, remainSeconds);
            }
        } catch (Exception e) {
            log.warn("登出时解析Token失败: {}", e.getMessage());
            // 解析失败也返回成功，不影响用户体验
        }


        return Result.success("登出成功");
    }
}
