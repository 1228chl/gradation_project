package com.graduationprojectordermanagementsystem.controller.admin;

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
    @Operation(summary = "查询所有订单信息")
    @RequireAnyRole({"admin"})
    @GetMapping("/list")
    public Result<PageResult<OrderVO>> getOrderList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize){
        PageResult<OrderVO> orderVO = orderService.getOrderList(pageNum, pageSize);
        if (orderVO != null){
            log.info("查询订单成功");
            return Result.success(orderVO);
        }
        return Result.error("查询订单失败或没有订单");
    }

    /**
     * 根据用户ID查询订单（分页）
     */
    @Operation(summary = "根据用户ID查询订单（分页）")
    @RequireAnyRole({"admin"})
    @GetMapping("/user/{id}")
    public PageResult<OrderVO> getOrdersByUserId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        return orderService.getOrdersByUserId(id, pageNum, pageSize);
    }


    @Operation(summary = "修改订单信息（支持部分字段）")
    @RequireAnyRole({"admin"})
    @PutMapping
    public Result<String> updateOrder(@RequestBody OrderDTO orderDTO) {
        try {
            if (orderService.updateOrder(orderDTO)){
                log.info("订单修改成功");
                return Result.success("订单修改成功");
            }
            return Result.error("订单修改失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("修改订单异常", e);
            return Result.error("订单修改失败：系统错误");
        }
    }

    @Operation(summary = "删除订单")
    @RequireAnyRole({"admin"})
    @DeleteMapping("/{id}")
    public Result<String> deleteOrder(@PathVariable("id") Long id) {
        try {
            if (orderService.deleteOrder(id)){
                log.info("订单删除成功");
                return Result.success("订单删除成功");
            }
            return Result.success("订单删除成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除订单异常", e);
            return Result.error("订单删除失败：系统错误");
        }
    }

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
}
