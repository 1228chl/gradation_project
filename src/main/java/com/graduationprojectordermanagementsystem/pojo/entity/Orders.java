package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders implements Serializable {
    @TableId(value = "id", type = IdType.ASSIGN_ID)//雪花算法
    private Long id;//订单id
    private String orderNumber;//订单编号
    private Long userId;//下单用户id
    private Long majorId;//需求专业id
    private Double price;// 价格
    private String title;//订单标题
    private String demand;//需求描述
    private Integer status;//状态:0-待支付,1-已支付,2-已完成,3-已取消
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;//更新时间

    // 序列化版本号
    @Serial
    private static final long serialVersionUID = 1L;
}
