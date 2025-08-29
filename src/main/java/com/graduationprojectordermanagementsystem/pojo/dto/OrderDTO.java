package com.graduationprojectordermanagementsystem.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO implements Serializable {
    private Long userId;//下单用户id
    private Long majorId;//需求专业id
    private Double price;// 价格
    private String title;//订单标题
    private String demand;//需求描述

}
