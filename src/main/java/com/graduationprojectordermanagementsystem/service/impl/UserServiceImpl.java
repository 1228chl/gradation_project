package com.graduationprojectordermanagementsystem.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.exception.*;
import com.graduationprojectordermanagementsystem.mapper.UserMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户登录
     */
    @Override
    public User login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String email = loginDTO.getEmail();

        //1. 根据用户名查询数据库中的数据
        User user = userMapper.findOneByUsernameOrEmail(username,email);

        //2.处理各种异常情况（用户名不存在、密码不对、账号被禁用）
        if (user == null){
            //用户名或邮箱不存在
            throw new AccountAndEmailNotFoundException(CommonContent.UserAndEmailNotExist);
        }

        if (!BCrypt.checkpw(password, user.getPassword())){
            //密码错误
            throw new PasswordErrorException(CommonContent.PasswordError);
        }

        if(user.getStatus().equals(StatusContent.DISABLE)){
            //账号被禁用
            throw new AccountLockedException(CommonContent.AccountLocked);
        }

        //3.更新数据库中的登录时间
        userMapper.update(null,new UpdateWrapper<User>()
                .eq("username",username)
                .set("last_login_time",new Date()));
        return user;
    }

    /**
     * 用户注册
     */
    @Override
    public String register(RegisterDTO registerDTO) {
        //1. 判断用户名或邮箱是否已注册
        User user = userMapper.findOneByUsernameOrEmail(registerDTO.getUsername(), registerDTO.getEmail());
        if (user != null) {
            if (user.getUsername().equals(registerDTO.getUsername())) {
                return CommonContent.UsernameAlreadyRegistered;
            }
            if (user.getEmail() != null && user.getEmail().equals(registerDTO.getEmail())) {
                return CommonContent.EmailAlreadyRegistered;
            }
        }
        //2.将用户信息插入数据库
        User newUser = new User();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPassword(BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt(12)));
        newUser.setPhone(registerDTO.getPhone());
        newUser.setCreateTime(new Date());
        newUser.setUpdateTime(new Date());
        newUser.setStatus(StatusContent.ENABLE);
        userMapper.insert(newUser);
        return CommonContent.RegisterSuccess;
    }
}
