package com.graduationprojectordermanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;

public interface UserService{
    UserVO getUserInfo(String username);

    User login(LoginDTO loginDTO);

    String register(RegisterDTO registerDTO);
}
