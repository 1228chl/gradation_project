package com.graduationprojectordermanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.UserDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    UserDTO getUserByUsername(String username);

    /**
     * 根据用户名或邮箱查询用户
     */
    User findOneByUsernameOrEmail(String username, String email);
}
