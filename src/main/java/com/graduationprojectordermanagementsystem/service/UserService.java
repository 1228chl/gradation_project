package com.graduationprojectordermanagementsystem.service;

import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.UserDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.pojo.vo.UserMajorVO;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.Result;

import java.util.List;

public interface UserService{
    UserVO getUserInfo(String username);

    User login(LoginDTO loginDTO);

    String register(RegisterDTO registerDTO);


    PageResult<UserVO> getUserList(Integer pageNum, Integer pageSize);

    boolean deleteUser(Long id);

    boolean updateUser(UserDTO userDTO);

    Result<String> addUserMajor(Long userId, Long majorId);

    Result<String> deleteUserMajor(Long userId, Long majorId);

    Result<List<UserMajorVO>> getLikeMajorList(Long userId);
}
