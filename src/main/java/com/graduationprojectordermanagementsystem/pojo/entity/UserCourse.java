package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_course")
public class UserCourse implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//自增ID
    private Long userId;//用户ID
    private Long courseId;//课程ID
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;//修改时间
    @Serial
    private static final long serialVersionUID = 1L;
}
