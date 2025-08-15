package com.graduationprojectordermanagementsystem.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
    private Date lastLoginTime;// 最后登录时间
    private Date createTime;// 创建时间
    private Date updateTime;// 更新时间

}
