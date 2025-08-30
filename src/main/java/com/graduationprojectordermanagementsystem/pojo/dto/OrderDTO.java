package com.graduationprojectordermanagementsystem.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO implements Serializable {
    private Long id;//订单id
    private String orderNumber;//订单编号
    private Long userId;//下单用户id
    private Long majorId;//需求专业id
    private Double price;// 价格
    private String title;//订单标题
    private String demand;//需求描述
    private Integer status;//订单状态

}
