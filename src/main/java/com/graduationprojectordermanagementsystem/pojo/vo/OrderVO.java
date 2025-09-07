package com.graduationprojectordermanagementsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO implements Serializable {
    private Long id;//订单id
    private String orderNumber;//订单编号
    private Double price;// 价格
    private String title;// 标题
    private String type;// 类型
    private String demand;// 需求
    private LocalDateTime createTime;// 创建时间
    private Integer status;// 状态
}
