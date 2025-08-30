package com.graduationprojectordermanagementsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.UserDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;
import com.graduationprojectordermanagementsystem.result.PageResult;

public interface UserService{
    UserVO getUserInfo(String username);

    User login(LoginDTO loginDTO);

    String register(RegisterDTO registerDTO);


    PageResult<UserVO> getUserList(Integer pageNum, Integer pageSize);

    boolean deleteUser(Long id);

    boolean updateUser(UserDTO userDTO);
}
