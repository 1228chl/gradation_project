package com.graduationprojectordermanagementsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemVO implements Serializable {
    private Long total_order_count;//总订单数
    private Long processing_order_count;//处理中的订单数
    private Long running_days;//网站运行时间
}
