package com.graduationprojectordermanagementsystem.controller.user;

import com.graduationprojectordermanagementsystem.pojo.dto.OrderDTO;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "user订单接口")
@RestController("userOrderController")
@RequestMapping("/api/user/order")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;

    @Operation(summary = "添加订单")
    @PostMapping("/add")
    public Result<String> addOrder(@RequestBody OrderDTO orderDTO) {
        log.info("添加订单");
        if (orderService.addOrder(orderDTO)){
            log.info("订单添加成功");
            return Result.success("添加订单成功");
        }
        return Result.error("添加订单失败");
    }

}
