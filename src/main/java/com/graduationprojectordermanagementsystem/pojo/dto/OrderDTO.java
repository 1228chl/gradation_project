package com.graduationprojectordermanagementsystem.pojo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO implements Serializable {
    private Long id;//订单id
    private String orderNumber;//订单编号
    @NotNull(message = "专业ID不能为空")
    private Long userId;//下单用户id
    @NotNull(message = "专业ID不能为空")
    private Long majorId;//需求专业id
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private Double price;// 价格
    @NotBlank(message = "标题不能为空")
    private String title;//订单标题
    @NotBlank(message = "需求不能为空")
    private String demand;//需求描述
    @NotBlank(message = "类型不能为空")
    private String type;//订单类型
    private Integer status;//订单状态

}
