package com.graduationprojectordermanagementsystem.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    @NotNull(message = "用户ID不能为空")
    private Long id;//用户id
    private String username;//用户名
    private String password;//密码
    private String email;//邮箱
    private String phone;//手机号
    private String avatar;//头像
    private String role;//角色
    private Integer status;//状态

    @Serial
    private static final long serialVersionUID = 1L;
}
