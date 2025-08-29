package com.graduationprojectordermanagementsystem.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.contents.RoleContent;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.exception.*;
import com.graduationprojectordermanagementsystem.mapper.UserMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;



    /**
     * 用户登录
     */
    @Override
    public User login(LoginDTO loginDTO) {
        String account = loginDTO.getAccount();


        //1. 根据用户名查询数据库中的数据（支持用户名或邮箱）
        User user = userMapper.findOneByUsernameOrEmail(account,account);

        //2.处理各种异常情况（用户名不存在、密码不对、账号被禁用）
        if (user == null){
            //用户名或邮箱不存在
            throw new AccountAndEmailNotFoundException(CommonContent.UserAndEmailNotExist);
        }
        // 3. 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())){
            //密码错误
            throw new PasswordErrorException(CommonContent.PasswordError);
        }
        // 4. 检查账号状态
        if(user.getStatus().equals(StatusContent.DISABLE)){
            //账号被禁用
            throw new AccountLockedException(CommonContent.AccountLocked);
        }

        //5.更新数据库中的登录时间
        log.info("更新用户最后登录时间");
        userMapper.update(null,new UpdateWrapper<User>()
                .eq("id",user.getId())
                .set("last_login_time", LocalDateTime.now()));
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
        newUser.setRole(RoleContent.USER);
        newUser.setStatus(StatusContent.ENABLE);
        newUser.setAvatar(CommonContent.DefaultAvatar);
        userMapper.insert(newUser);
        return CommonContent.RegisterSuccess;
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public UserVO getUserInfo(String username) {
        User user = userMapper.getUserByUsername(username);
        return new UserVO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getStatus(),
                user.getRole(),
                user.getLastLoginTime());
    }

    /**
     * 获取用户列表
     */
    @Override
    public PageResult<UserVO> getUserList(Integer pageNum, Integer pageSize) {
        // 1. 创建 MP 的分页对象
        Page<User> page = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询（你可以加条件，如状态、用户名等）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 示例：只查启用的用户
        // wrapper.eq(User::getStatus, StatusContent.ENABLE);

        Page<User> userPage = userMapper.selectPage(page, wrapper);

        // 3. 转换实体为 VO 列表
        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            // 手动处理 createTime 等字段（如果类型不匹配）
            return vo;
        }).toList();

        // 4. 封装并返回 PageResult
        return PageResult.of(
                userPage.getTotal(),    // 总数
                (int) userPage.getCurrent(), // 当前页
                (int) userPage.getSize(),    // 每页大小
                voList                   // 数据列表
        );
    }
}
