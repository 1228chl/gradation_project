package com.graduationprojectordermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.mapper.OrderMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.OrderDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.Orders;
import com.graduationprojectordermanagementsystem.pojo.vo.OrderVO;
import com.graduationprojectordermanagementsystem.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        //创建订单编号，订单编号格式：专业id+用户id时间戳+随机数
        String orderNumber = orderDTO.getMajorId().toString() +"-"+ orderDTO.getUserId().toString() +"-"+ System.currentTimeMillis() +"-"+ (int)(Math.random() * 1000);
        log.info("订单编号: {}", orderNumber);
        Orders orders = new Orders();
        //TO DO 订单编号需要专业code加其他组成，先完成课程专业接口
        orders.setOrderNumber(orderNumber);//订单编号
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

    /**
     * 根据id获取用户订单

     */
    @Override
    public List<OrderVO> getOrderById(Long id) {
        log.info("开始获取用户订单，用户ID: {}", id);

        // 判断id是否为空
        if (id == null || id <= 0) {
            log.warn("用户ID为空或无效");
            return Collections.emptyList(); // 返回空列表，比 null 更安全
        }

        // 1. 查询该用户的所有订单（假设 orders 表中有 user_id 字段）
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, id) // 根据用户ID查询
                .orderByDesc(Orders::getCreateTime); // 按创建时间倒序

        List<Orders> ordersList = orderMapper.selectList(wrapper);

        // 2. 如果没有订单，返回空列表
        if (ordersList == null || ordersList.isEmpty()) {
            log.info("用户 {} 没有订单", id);
            return Collections.emptyList();
        }

        // 3. 将 Orders 实体转换为 OrderVO（使用 Stream 或循环）
        List<OrderVO> orderVOList = ordersList.stream().map(order -> {
            OrderVO vo = new OrderVO();
            // 手动拷贝字段，或使用 BeanUtils.copyProperties(vo, order);
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).toList();

        log.info("查询到 {} 条订单", orderVOList.size());
        return orderVOList;
    }
}
