package com.graduationprojectordermanagementsystem.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegisterDTO implements Serializable {
    private String username;
    private String password;
    private String email;
    private String phone;
}
