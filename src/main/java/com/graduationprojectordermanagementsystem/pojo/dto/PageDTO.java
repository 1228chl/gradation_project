package com.graduationprojectordermanagementsystem.pojo.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduationprojectordermanagementsystem.pojo.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO implements Serializable {
    private Page<Orders> page;// 分页
    private Page<Orders> result;// 结果
    private List<Orders> records;// 记录
    private Long total;// 总记录数
    private Integer current;// 当前页
    private Integer size;// 每页记录数
}
