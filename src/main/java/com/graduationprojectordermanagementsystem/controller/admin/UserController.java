package com.graduationprojectordermanagementsystem.controller.admin;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.UserService;
import com.graduationprojectordermanagementsystem.util.JwtUtils;
import com.graduationprojectordermanagementsystem.util.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户接口")
@RestController("adminUserController")
@RequestMapping("/api/admin")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @Operation(summary = "查询所有用户信息")
    @GetMapping("/list")
    @RequireAnyRole({"admin"})
    public Result<PageResult<UserVO>> getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize){
        log.info("获取用户列表");
        PageResult<UserVO> userList = userService.getUserList(pageNum, pageSize);
        log.info("用户列表：{}", userList);
        return Result.success(userList);
    }


}
