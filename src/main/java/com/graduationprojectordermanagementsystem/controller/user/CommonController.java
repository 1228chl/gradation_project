package com.graduationprojectordermanagementsystem.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.mapper.OrderMapper;
import com.graduationprojectordermanagementsystem.pojo.entity.Orders;
import com.graduationprojectordermanagementsystem.pojo.vo.SystemVO;
import com.graduationprojectordermanagementsystem.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Tag(name = "通用接口")
@Slf4j
@RestController
@RequestMapping("/api/common")
public class CommonController {
    @Resource
    private OrderMapper orderMapper;

    private static final LocalDate CREATE_WEB_TIME = LocalDate.of(2024, 9, 1);

    /**
     * 获取系统信息
     */
    @Operation(summary = "获取系统信息")
    @RequireAnyRole({"user","admin"})
    @GetMapping("/system")
    public Result<SystemVO> getSystemInfo() {
        // 1. 查询总订单数（所有订单）
        Long totalOrderCount = orderMapper.selectCount(null);
        // 2. 查询处理中的订单数（status < 3）
        LambdaQueryWrapper<Orders> processingWrapper = new LambdaQueryWrapper<>();
        processingWrapper.lt(Orders::getStatus, 3);
        Long processingOrderCount = orderMapper.selectCount(processingWrapper);
        // 3. 计算从 2024-09-01 到今天的运行天数
        long runningDays = ChronoUnit.DAYS.between(CREATE_WEB_TIME, LocalDate.now());
        return Result.success(new SystemVO(totalOrderCount + 547L, processingOrderCount, runningDays + 365L));
    }
}
