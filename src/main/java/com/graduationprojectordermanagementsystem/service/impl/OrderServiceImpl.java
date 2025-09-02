package com.graduationprojectordermanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.mapper.OrderMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.OrderDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.Orders;
import com.graduationprojectordermanagementsystem.pojo.vo.OrderVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
        orders.setType(orderDTO.getType());// 订单类型
        orders.setStatus(StatusContent.UNPAID);// 状态
        try {

            return orderMapper.insert(orders) > 0;
        } catch (Exception e) {
            log.error("添加订单失败: {}", e.getMessage(), e); // 记录错误日志
            return false; // 插入异常时返回 false
        }
    }

    @Override
    public PageResult<OrderVO> getOrderList(Integer pageNum, Integer pageSize) {
        log.info("开始分页查询所有订单，页码：{}, 每页：{}", pageNum, pageSize);

        // 1. 构建查询条件：按创建时间倒序
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getCreateTime);

        // 2. 创建分页对象
        Page<Orders> page = new Page<>(pageNum, pageSize);

        // 3. 执行分页查询
        Page<Orders> result = orderMapper.selectPage(page, wrapper);

        List<Orders> records = result.getRecords();// 获取查询结果
        long total = result.getTotal();// 获取总记录数
        int current = (int) result.getCurrent();// 获取当前页码
        int size = (int) result.getSize();// 获取每页记录数

        log.info("分页查询完成，总记录数：{}，当前页数据量：{}", total, records.size());

        // 4. 转换为 VO 列表
        List<OrderVO> voList = records.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).toList();

        // 5. 构建并返回 PageResult
        return PageResult.of(total, current, size, voList);
    }

    @Override
    public PageResult<OrderVO> getOrdersByUserId(Long userId, Integer pageNum, Integer pageSize) {
        log.info("开始查询用户 {} 的订单，页码：{}, 每页：{}", userId, pageNum, pageSize);

        // 1. 参数校验
        if (userId == null || userId <= 0) {
            log.warn("用户ID无效：{}", userId);
            return PageResult.of(0L, 1, pageSize != null ? pageSize : 10, List.of());
        }

        // 2. 构建查询条件：用户ID + 按创建时间倒序
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime);

        // 3. 创建分页对象并查询
        Page<Orders> page = new Page<>(pageNum, pageSize);
        Page<Orders> result = orderMapper.selectPage(page, wrapper);

        List<Orders> records = result.getRecords();
        long total = result.getTotal();
        int current = (int) result.getCurrent();
        int size = (int) result.getSize();

        // 4. 转换为 VO 列表
        List<OrderVO> voList = records.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).collect(Collectors.toList());

        log.info("用户 {} 共有 {} 条订单，当前页返回 {} 条", userId, total, voList.size());

        // 5. 返回分页结果
        return PageResult.of(total, current, size, voList);
    }

    @Override
    public boolean updateOrder(OrderDTO orderDTO) {
        log.info("开始修改订单：{}", orderDTO);

        // 1. 参数校验
        if (orderDTO == null || orderDTO.getId() == null || orderDTO.getId() <= 0) {
            log.warn("修改订单失败，订单ID无效：{}", orderDTO);
            throw new IllegalArgumentException("订单ID不能为空且必须大于0");
        }

        // 2. 查询原订单是否存在
        Orders existingOrder = orderMapper.selectById(orderDTO.getId());
        if (existingOrder == null) {
            log.warn("修改订单失败，订单不存在，ID：{}", orderDTO.getId());
            throw new IllegalArgumentException("订单不存在");
        }

        // 3. 构建要更新的 Orders 对象（只设置非 null 字段）
        Orders order = new Orders();
        order.setId(orderDTO.getId()); // 主键用于 WHERE id=?

        // 只有传了才更新（null 不更新）
        if (orderDTO.getUserId() != null) {
            order.setUserId(orderDTO.getUserId());
        }
        if (orderDTO.getMajorId() != null) {
            order.setMajorId(orderDTO.getMajorId());
        }
        if (orderDTO.getPrice() != null) {
            order.setPrice(orderDTO.getPrice());
        }
        if (orderDTO.getTitle() != null) {
            order.setTitle(orderDTO.getTitle());
        }
        if (orderDTO.getDemand() != null) {
            order.setDemand(orderDTO.getDemand());
        }
        if (orderDTO.getType() != null) {
            order.setType(orderDTO.getType());
        }
        if (orderDTO.getStatus() != null) {
            order.setStatus(orderDTO.getStatus());
        }

        // 4. 执行更新（MyBatis-Plus 自动忽略 null 字段）
        int result = orderMapper.updateById(order);

        if (result > 0) {
            log.info("订单修改成功，ID：{}", orderDTO.getId());
        } else {
            log.error("订单修改失败，ID：{}", orderDTO.getId());
        }

        return result > 0;
    }

    @Override
    public boolean deleteOrder(Long id) {
        log.info("开始物理删除订单，ID：{}", id);

        // 校验参数
        if (id == null || id <= 0) {
            log.warn("删除订单失败，订单ID无效：{}", id);
            throw new IllegalArgumentException("订单ID不能为空且必须大于0");
        }

        // 查询是否存在
        Orders existingOrder = orderMapper.selectById(id);
        if (existingOrder == null) {
            log.warn("删除订单失败，订单不存在，ID：{}", id);
            throw new IllegalArgumentException("订单不存在");
        }

        // 执行物理删除
        int result = orderMapper.deleteById(id);

        if (result > 0) {
            log.info("订单物理删除成功，ID：{}", id);
            return true;
        } else {
            log.error("订单物理删除失败，数据库影响行数为0，ID：{}", id);
            return false;
        }
    }


}
