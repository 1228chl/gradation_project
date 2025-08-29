package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("major")
public class Major implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;// 主键
    private String majorName;// 专业名称
    private String majorCode;// 专业代码
    private String majorDesc;// 专业描述
    private Integer majorStatus;// 专业状态
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;// 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;// 更新时间
    // 序列化版本号
    @Serial
    private static final long serialVersionUID = 1L;
}
