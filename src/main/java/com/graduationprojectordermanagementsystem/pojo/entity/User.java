package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;// 用户id
    private String username;// 用户名
    private String password;// 密码
    private String email;// 邮箱
    private String phone;// 手机号
    private Integer status;// 状态
    private String role;// 角色
    private LocalDateTime lastLoginTime;// 最后登录时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;// 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;// 更新时间

    // 序列化版本号
    @Serial
    private static final long serialVersionUID = 1L;

}
