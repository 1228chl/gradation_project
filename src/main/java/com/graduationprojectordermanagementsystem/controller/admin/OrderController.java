package com.graduationprojectordermanagementsystem.controller.admin;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "admin订单接口")
@RestController("adminOrderController")
@RequestMapping("/api/admin/order")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;

    /**
     * 查询所有订单信息
     */
//    @Operation(summary = "查询所有订单信息")
//    @RequireAnyRole({"admin"})
//    @GetMapping("/list")


}
