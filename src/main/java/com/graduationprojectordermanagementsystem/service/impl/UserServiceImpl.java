package com.graduationprojectordermanagementsystem.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.contents.RoleContent;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.exception.*;
import com.graduationprojectordermanagementsystem.mapper.MajorMapper;
import com.graduationprojectordermanagementsystem.mapper.UserMajorMapper;
import com.graduationprojectordermanagementsystem.mapper.UserMapper;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.UserDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.Major;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import com.graduationprojectordermanagementsystem.pojo.entity.UserMajor;
import com.graduationprojectordermanagementsystem.pojo.vo.UserMajorVO;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserMajorMapper userMajorMapper;
    @Resource
    private MajorMapper majorMapper;



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

    /**
     * 删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 启用事务，异常回滚
    public boolean deleteUser(Long id) {
        log.info("开始删除用户，用户ID：{}", id);

        // 1. 参数校验
        if (id == null || id <= 0) {
            log.warn("删除用户失败，非法用户ID：{}", id);
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }

        // 2. 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            log.warn("删除用户失败，用户不存在，ID：{}", id);
            throw new IllegalArgumentException("用户不存在");
        }

        // 3. 【安全增强】禁止删除超级管理员（可选）
        if ("admin".equals(user.getRole())) {
            log.warn("禁止删除超级管理员用户，ID：{}", id);
            throw new SecurityException("禁止删除超级管理员账号");
        }

        // 4. 执行物理删除
        try {
            int deleteCount = userMapper.deleteById(id);
            if (deleteCount > 0) {
                log.info("用户删除成功，ID：{}，用户名：{}", id, user.getUsername());
                return true;
            } else {
                log.error("用户删除失败，数据库未删除任何记录，ID：{}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除用户时发生异常，ID：{}", id, e);
            throw new RuntimeException("删除用户失败", e);
        }
    }

    @Override
    public boolean updateUser(UserDTO userDTO) {
        log.info("开始修改用户信息：{}", userDTO);
        // 1. 校验参数
        if (userDTO == null || userDTO.getId() == null || userDTO.getId() <= 0) {
            log.warn("更新用户失败，用户ID无效：{}", userDTO);
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }

        // 2. 查询原用户是否存在
        User existingUser = userMapper.selectById(userDTO.getId());
        if (existingUser == null) {
            log.warn("更新用户失败，用户不存在，ID：{}", userDTO.getId());
            throw new IllegalArgumentException("用户不存在");
        }

        // 3. 构建要更新的 User 实体（只设置非 null 字段）
        User user = new User();
        user.setId(userDTO.getId()); // 主键用于 WHERE 条件

        // 普通字段直接复制
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (userDTO.getStatus() != null) {
            user.setStatus(userDTO.getStatus());
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }

        // 🔐 密码特殊处理：如果传了新密码，才加密并设置
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            user.setPassword(BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt(12)));
            log.info("密码已加密，用户ID：{}", userDTO.getId());
        }

        // 4. 执行更新
        int updateResult = userMapper.updateById(user);

        if (updateResult > 0) {
            log.info("用户更新成功，ID：{}", userDTO.getId());
        } else {
            log.error("用户更新失败，但无异常，ID：{}", userDTO.getId());
        }

        return updateResult > 0;
    }

    /**
     * 添加我喜欢功能
     */
    @Override
    public Result<String> addUserMajor(Long userId, Long majorId){
        log.info("添加我喜欢功能，用户id {}，专业id {}", userId, majorId);

        // 参数校验
        if (userId == null || majorId == null) {
            return Result.error("用户ID或专业ID不能为空");
        }

        // 查询用户是否存在（防止攻击）
        LambdaQueryWrapper<User> queryWrapperUser = new LambdaQueryWrapper<>();
        queryWrapperUser.eq(User::getId, userId);
        if (userMapper.selectOne(queryWrapperUser) == null) {
            return Result.error("用户不存在");
        }
        // 验证专业是否存在（防止攻击）
        LambdaQueryWrapper<Major> queryWrapperMajor = new LambdaQueryWrapper<>();
        queryWrapperMajor.eq(Major::getId, majorId);
        if (majorMapper.selectOne(queryWrapperMajor) == null) {
            return Result.error("专业不存在");
        }

        // 查询是否已存在（防止重复添加）
        LambdaQueryWrapper<UserMajor> queryWrapperUserMajor = new LambdaQueryWrapper<>();
        queryWrapperUserMajor.eq(UserMajor::getUserId, userId)
                .eq(UserMajor::getMajorId, majorId);

        if (userMajorMapper.selectCount(queryWrapperUserMajor) > 0) {
            return Result.error("该专业已添加至“我喜欢”，请勿重复添加");
        }

        // 构建实体类保存
        UserMajor userMajor = new UserMajor();
        userMajor.setUserId(userId);
        userMajor.setMajorId(majorId);
        // createTime 由 MyBatis-Plus 自动填充（@TableField(fill = FieldFill.INSERT)）

        int save = userMajorMapper.insert(userMajor);
        if (save>0) {
            return Result.success("已成功添加至“我喜欢”");
        } else {
            return Result.error("添加失败，请重试");
        }
    }

    /**
     * 我喜欢功能(取消)
     */
    @Override
    public Result<String> deleteUserMajor(Long userId, Long majorId) {
        log.info("取消我喜欢功能，用户id {}，专业id {}", userId, majorId);
        if (majorId == null) {
            return Result.error("专业ID不能为空");
        }
        // 查询用户是否存在（防止攻击）
        LambdaQueryWrapper<User> queryWrapperUser = new LambdaQueryWrapper<>();
        queryWrapperUser.eq(User::getId, userId);
        if (userMapper.selectOne(queryWrapperUser) == null) {
            return Result.error("用户不存在");
        }
        // 验证专业是否存在（防止攻击）
        LambdaQueryWrapper<Major> queryWrapperMajor = new LambdaQueryWrapper<>();
        queryWrapperMajor.eq(Major::getId, majorId);
        if (majorMapper.selectOne(queryWrapperMajor) == null) {
            return Result.error("专业不存在");
        }
        // 查询是否不存在（防止重复添加）
        LambdaQueryWrapper<UserMajor> queryWrapperUserMajor = new LambdaQueryWrapper<>();
        queryWrapperUserMajor.eq(UserMajor::getUserId, userId)
                .eq(UserMajor::getMajorId, majorId);

        if (userMajorMapper.selectCount(queryWrapperUserMajor) == 0) {
            return Result.error("已取消“我喜欢”，请勿重复取消");
        }

        int save = userMajorMapper.delete(queryWrapperUserMajor);
        if (save>0) {
            return Result.success("已成功取消“我喜欢”");
        } else {
            return Result.error("取消失败，请重试");
        }
    }

    /**
     * 我喜欢 列表
     */
    @Override
    public Result<List<UserMajorVO>> getLikeMajorList(Long userId) {
        log.info("通过用户ID查询我喜欢列表，用户id: {}", userId);

        // 1. 可选：验证用户是否存在
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getId, userId);
        if (userMapper.selectOne(userWrapper) == null) {
            return Result.error("用户不存在");
        }

        // 2. 查询用户喜欢的专业关联记录
        LambdaQueryWrapper<UserMajor> wrapper = new LambdaQueryWrapper<>();

        // 直接查询 user_major 表，只取 major_id 和 create_time
        wrapper.select(UserMajor::getMajorId, UserMajor::getCreateTime) // 只查需要的字段
                .eq(UserMajor::getUserId, userId)
                .orderByDesc(UserMajor::getCreateTime);

        List<UserMajor> list = userMajorMapper.selectList(wrapper);

        // 转换为 VO
        List<UserMajorVO> voList = list.stream().map(um -> {
            UserMajorVO vo = new UserMajorVO();
            vo.setMajorId(um.getMajorId());
            vo.setCreateTime(um.getCreateTime()); // 假设 createTime 是 Date 或 Timestamp
            return vo;
        }).collect(Collectors.toList());

        if (voList.isEmpty()) {
            log.info("用户 {} 没有添加任何我喜欢的专业", userId);
            return Result.success(voList);
        }
        log.info("用户 {} 共有 {} 条我喜欢记录", userId, voList.size());
        return Result.success(voList);
    }

}
