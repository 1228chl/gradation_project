package com.graduationprojectordermanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;

public interface UserService{
    User login(LoginDTO loginDTO);

    String register(RegisterDTO registerDTO);
}
