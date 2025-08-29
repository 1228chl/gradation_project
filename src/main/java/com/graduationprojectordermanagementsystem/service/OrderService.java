package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.dto.OrderDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    Boolean addOrder(OrderDTO orderDTO);

    List<OrderVO> getOrderById(Long id);
}
