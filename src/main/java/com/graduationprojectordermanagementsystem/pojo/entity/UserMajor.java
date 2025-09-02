package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@TableName("user_major")
public class UserMajor implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//主键
    private Long userId;//用户ID
    private Long majorId;//专业ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
