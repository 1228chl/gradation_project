package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;//课程ID
    private String courseName;//课程名称
    private String courseCode;//课程代码
    private String courseDesc;//课程描述
    private String courseUrl;//课程链接
    private Integer courseStatus;//课程状态
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;//创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;//修改时间
    @Serial
    private static final long serialVersionUID = 1L;
}
