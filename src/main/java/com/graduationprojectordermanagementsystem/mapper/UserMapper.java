package com.graduationprojectordermanagementsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(@Param("username") String username);

    /**
     * 根据用户名或邮箱查询用户
     */
    User findOneByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
}
