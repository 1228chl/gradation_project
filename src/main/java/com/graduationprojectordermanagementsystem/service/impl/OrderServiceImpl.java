package com.graduationprojectordermanagementsystem.service.impl;

import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.mapper.OrderMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.OrderDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.Orders;
import com.graduationprojectordermanagementsystem.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;

    /**
     * 添加订单
     */
    @Override
    public Boolean addOrder(OrderDTO orderDTO) {
        log.info("开始添加订单");
        Orders orders = new Orders();
        //TODO 订单编号需要专业code加其他组成，先完成课程专业接口
//        orders.setOrderNumber();//订单编号
        orders.setUserId(orderDTO.getUserId());//用户id
        orders.setMajorId(orderDTO.getMajorId());//专业id
        orders.setPrice(orderDTO.getPrice());// 价格
        orders.setTitle(orderDTO.getTitle());// 标题
        orders.setDemand(orderDTO.getDemand());// 需求
        orders.setStatus(StatusContent.UNPAID);// 状态
        try {

            return orderMapper.insert(orders) > 0;
        } catch (Exception e) {
            log.error("添加订单失败: {}", e.getMessage(), e); // 记录错误日志
            return false; // 插入异常时返回 false
        }
    }
}
