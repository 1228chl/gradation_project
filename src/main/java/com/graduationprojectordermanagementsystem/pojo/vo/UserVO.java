package com.graduationprojectordermanagementsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO implements Serializable {
    private Long id;//用户id
    private String username;//用户名
    private String email;//邮箱
    private String phone;//手机号
    private String avatar;//头像
    private Integer status;//状态
    private String role;//角色
    private LocalDateTime lastLoginTime;//最后登录时间
}
