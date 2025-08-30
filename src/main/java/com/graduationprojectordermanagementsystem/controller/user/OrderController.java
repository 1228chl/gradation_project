package com.graduationprojectordermanagementsystem.controller.user;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.pojo.dto.OrderDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.OrderVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Tag(name = "user订单接口")
@RestController("userOrderController")
@RequestMapping("/api/user/order")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;

    /**
     * 添加订单
     */
    @Operation(summary = "添加订单")
    @RequireAnyRole({"user","admin"})
    @PostMapping
    public Result<String> addOrder(@RequestBody OrderDTO orderDTO) {
        log.info("添加订单");
        if (orderService.addOrder(orderDTO)){
            log.info("订单添加成功");
            return Result.success("添加订单成功");
        }
        return Result.error("添加订单失败");
    }

    @Operation(summary = "查询用户订单")
    @RequireAnyRole({"user","admin"})
    @GetMapping("/{id}")
    public Result<PageResult<OrderVO>> getOrderById(@PathVariable("id") Long id,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询用户订单");
        PageResult<OrderVO> orderVO = orderService.getOrdersByUserId(id, pageNum, pageSize);
        if (orderVO != null){
            log.info("查询用户订单成功");
            return Result.success(orderVO);
        }
        return Result.error("查询用户订单失败");
    }

}
