package com.graduationprojectordermanagementsystem.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "账号（用户名/邮箱）不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;
}
