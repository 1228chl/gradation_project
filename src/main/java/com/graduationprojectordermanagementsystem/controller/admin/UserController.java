package com.graduationprojectordermanagementsystem.controller.admin;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.dto.UserDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "删除用户信息")
    @DeleteMapping("/{id}")
    @RequireAnyRole({"admin"})
    public Result<String> deleteUser(@PathVariable("id") Long id){
        log.info("删除用户：{}", id);

        if (userService.deleteUser(id)){
            return Result.success("删除用户成功");
        }
        return Result.error("删除失败");
    }

    @Operation(summary = "修改用户信息")
    @PutMapping
    @RequireAnyRole({"admin"})
    public Result<String> updateUser(@Valid @RequestBody UserDTO userDTO){
        log.info("修改用户信息：{}", userDTO);
        if (userService.updateUser(userDTO)){
            return Result.success("修改用户信息成功");
        }
        return Result.error("修改用户信息失败");
    }


}
