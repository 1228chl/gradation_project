package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.dto.OrderDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.OrderVO;
import com.graduationprojectordermanagementsystem.result.PageResult;


public interface OrderService {
    Boolean addOrder(OrderDTO orderDTO);

    PageResult<OrderVO> getOrderList(Integer pageNum, Integer pageSize);

    PageResult<OrderVO> getOrdersByUserId(Long id, Integer pageNum, Integer pageSize);

    boolean updateOrder(OrderDTO orderDTO);

    boolean deleteOrder(Long id);
}
