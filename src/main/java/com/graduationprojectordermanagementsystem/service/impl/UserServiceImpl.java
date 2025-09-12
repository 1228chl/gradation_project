package com.graduationprojectordermanagementsystem.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.contents.RoleContent;
import com.graduationprojectordermanagementsystem.contents.StatusContent;
import com.graduationprojectordermanagementsystem.exception.*;
import com.graduationprojectordermanagementsystem.mapper.*;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.RegisterDTO;
import com.graduationprojectordermanagementsystem.pojo.dto.UserDTO;
import com.graduationprojectordermanagementsystem.pojo.entity.*;
import com.graduationprojectordermanagementsystem.pojo.vo.UserCourseVO;
import com.graduationprojectordermanagementsystem.pojo.vo.UserVO;
import com.graduationprojectordermanagementsystem.result.PageResult;
import com.graduationprojectordermanagementsystem.result.Result;
import com.graduationprojectordermanagementsystem.result.ResultCode;
import com.graduationprojectordermanagementsystem.service.UserService;
import com.graduationprojectordermanagementsystem.util.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserCourseMapper userCourseMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private FileMapper fileMapper;



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
            log.warn("登录失败：账号或邮箱不存在，account={}", account);
            throw new AccountAndEmailNotFoundException(CommonContent.UserAndEmailNotExist);
        }
        // 3. 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())){
            //密码错误
            log.warn("登录失败：密码错误，account={}", account);
            throw new PasswordErrorException(CommonContent.PasswordError);
        }
        // 4. 检查账号状态
        if(user.getStatus().equals(StatusContent.DISABLE)){
            //账号被禁用
            log.warn("登录失败：账号被禁用，account={}", account);
            throw new AccountLockedException(CommonContent.AccountLocked);
        }
        // ✅ 标记需要更新登录时间（后续由 AOP 或事件机制处理）
        log.info("用户登录成功，userId={}", user.getId());

        //5.更新数据库中的登录时间
        log.info("更新用户最后登录时间{}", LocalDateTime.now());
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
                throw new BaseException(CommonContent.UsernameAlreadyRegistered);
            }
            if (user.getEmail() != null && user.getEmail().equals(registerDTO.getEmail())) {
                throw new BaseException(CommonContent.EmailAlreadyRegistered);
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
        log.info("获取用户信息，username={}", username);
        if (username == null) {
            throw new BaseException(ResultCode.VALIDATE_FAILED ,"用户名不能为空");
        }
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
        // 3. 提取课程 ID 列表，用于统计 likeCount
        List<Long> courseIdList = userPage.getRecords().stream()
                .map(User::getId)
                .toList();

        // 如果没有课程，避免后续 SQL 报错
        if (courseIdList.isEmpty()) {
            List<UserVO> emptyList = new ArrayList<>();
            return PageResult.of(userPage.getTotal(), pageNum, pageSize, emptyList);
        }

        // 4. 转换实体为 VO 列表
        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            // 手动处理 createTime 等字段（如果类型不匹配）
            return vo;
        }).toList();

        // 5. 封装并返回 PageResult
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

    /**
     * 修改用户信息
     */
    @Override
    public boolean updateUser(UserDTO userDTO) {
        log.info("开始修改用户信息：{}", userDTO);
        // 1. 校验参数
        if (userDTO == null || userDTO.getId() == null || userDTO.getId() <= 0) {
            log.warn("更新用户失败，用户ID无效：{}", userDTO);
            throw new BaseException("用户ID不能为空且必须大于0");
        }

        // 2. 查询原用户是否存在
        User existingUser = userMapper.selectById(userDTO.getId());
        if (existingUser == null) {
            log.warn("更新用户失败，用户不存在，ID：{}", userDTO.getId());
            throw new BaseException("用户不存在");
        }

        // 3. 获取当前登录用户信息
        String currentRole = UserContext.getRole();
        Long currentUserId = UserContext.getUserId();

        boolean isAdmin = RoleContent.ADMIN.equals(currentRole);
        log.info("当前用户ID：{}，角色：{}，尝试修改用户ID：{}", currentUserId, currentRole, userDTO.getId());

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
            log.info("头像已更新，用户ID：{}", userDTO.getId());
            String oldAvatar = existingUser.getAvatar();
            if (oldAvatar != null && !oldAvatar.equals(user.getAvatar())) {
                UploadFile oldFile = fileMapper.selectByFileUuid(oldAvatar);
                if (oldFile != null) {
                    // 可选：删除物理文件（本地 or OSS）
                    // fileStorageService.deleteFile(oldFile.getFilePath());

                    // 删除数据库记录
                    int deleteCount = fileMapper.deleteByFileUuid(oldAvatar);
                    if (deleteCount > 0) {
                        log.info("已删除旧头像文件记录，文件名：{}", oldAvatar);
                    } else {
                        log.warn("删除旧头像文件记录失败（可能已被删除），文件名：{}", oldAvatar);
                    }
                } else {
                    log.warn("未找到旧头像对应的 uploadFile 记录，文件名：{}", oldAvatar);
                }
            }else {
                log.info("旧头像为空或与新头像相同，无需删除：{}", oldAvatar);
            }
        }

        // 🔐 密码特殊处理：如果传了新密码，才加密并设置
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            user.setPassword(BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt(12)));
            log.info("密码已加密，用户ID：{}", userDTO.getId());
        }

        // ⚠️ 敏感字段：只有管理员可以修改
        if (isAdmin) {
            if (userDTO.getRole() != null) {
                user.setRole(userDTO.getRole());
            }
            if (userDTO.getStatus() != null) {
                user.setStatus(userDTO.getStatus());
            }
        } else {
            // 普通用户：只能修改自己的信息
            if (!currentUserId.equals(userDTO.getId())) {
                log.warn("越权操作：用户 {} 尝试修改用户 {}", currentUserId, userDTO.getId());
                throw new BaseException(ResultCode.FORBIDDEN ,"不允许越权操作");
            }

            // 普通用户不能修改 role 和 status
            if (userDTO.getRole() != null || userDTO.getStatus() != null) {
                log.warn("普通用户 {} 尝试修改受限字段：role={}, status={}",
                        currentUserId, userDTO.getRole(), userDTO.getStatus());
                throw new BaseException(ResultCode.FORBIDDEN,"无法修改角色或状态");
            }
            // 不设置 role 和 status
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
    public Result<String> addUserCourse(Long userId, Long courseId){
        log.info("添加我喜欢功能，用户id {}，课程id {}", userId, courseId);

        // 参数校验
        if (userId == null || courseId == null) {
            return Result.error(ResultCode.VALIDATE_FAILED ,"用户ID或课程ID不能为空");
        }

        // 查询用户是否存在（防止攻击）
        if (isUserExist(userId)) {
            return Result.error(ResultCode.VALIDATE_FAILED ,"用户不存在");
        }
        // 验证专业是否存在（防止攻击）
        if (isCourseExist(courseId)) {
            return Result.error(ResultCode.VALIDATE_FAILED ,"课程不存在");
        }

        // 查询是否已存在（防止重复添加）
        LambdaQueryWrapper<UserCourse> queryWrapperUserCourse = isUserCourseExist(userId, courseId);

        if (userCourseMapper.selectCount(queryWrapperUserCourse) > 0) {
            return Result.error("该专业已添加至“我喜欢”，请勿重复添加");
        }

        // 构建实体类保存
        UserCourse userCourse = new UserCourse();
        userCourse.setUserId(userId);
        userCourse.setCourseId(courseId);
        // createTime 由 MyBatis-Plus 自动填充（@TableField(fill = FieldFill.INSERT)）

        int save = userCourseMapper.insert(userCourse);
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
    public Result<String> deleteUserCourse(Long userId, Long courseId) {
        log.info("取消我喜欢功能，用户id {}，课程id {}", userId, courseId);
        if (courseId == null) {
            return Result.error(ResultCode.VALIDATE_FAILED ,"课程ID不能为空");
        }
        // 查询用户是否存在（防止攻击）

        if (isUserExist(userId)) {
            return Result.error(ResultCode.VALIDATE_FAILED ,"用户不存在");
        }
        // 验证专业是否存在（防止攻击）
        if (isCourseExist(courseId)) {
            return Result.error(ResultCode.VALIDATE_FAILED ,"课程不存在");
        }
        // 查询是否不存在（防止重复添加）
        LambdaQueryWrapper<UserCourse> queryWrapperUserCourse = isUserCourseExist(userId, courseId);
        if (userCourseMapper.selectCount(queryWrapperUserCourse) == 0) {
            return Result.error("已取消“我喜欢”，请勿重复取消");
        }

        int save = userCourseMapper.delete(queryWrapperUserCourse);
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
    public Result<List<UserCourseVO>> getLikeCourseList(Long userId) {
        log.info("通过用户ID查询我喜欢列表，用户id: {}", userId);

        // 1. 可选：验证用户是否存在
        if (isUserExist(userId)) {
            return Result.error(ResultCode.VALIDATE_FAILED ,"用户不存在");
        }

        // 2. 查询用户喜欢的专业关联记录
        LambdaQueryWrapper<UserCourse> wrapper = new LambdaQueryWrapper<>();

        // 直接查询 user_major 表，只取 major_id 和 create_time
        wrapper.select(UserCourse::getCourseId, UserCourse::getCreateTime) // 只查需要的字段
                .eq(UserCourse::getUserId, userId)
                .orderByDesc(UserCourse::getCreateTime);

        List<UserCourse> list = userCourseMapper.selectList(wrapper);

        // 转换为 VO
        List<UserCourseVO> voList = list.stream().map(um -> {
            UserCourseVO vo = new UserCourseVO();
            vo.setCourseId(um.getCourseId());
            vo.setCreateTime(um.getCreateTime()); // 假设 createTime 是 Date 或 Timestamp
            return vo;
        }).collect(Collectors.toList());

        if (voList.isEmpty()) {
            log.info("用户 {} 没有添加任何我喜欢的课程", userId);
            return Result.success(voList);
        }
        log.info("用户 {} 共有 {} 条我喜欢记录", userId, voList.size());
        return Result.success(voList);
    }

    /**
     * 判断用户是否存在
     */
    private boolean isUserExist(Long userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        return userMapper.selectOne(queryWrapper) == null;
    }

    /**
     * 验证专业是否存在
     */
    private boolean isCourseExist(Long courseId) {
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getId, courseId);
        return courseMapper.selectOne(queryWrapper) == null;
    }

    /**
     * 验证用户-课程关联记录是否存在
     */
    private LambdaQueryWrapper<UserCourse> isUserCourseExist(Long userId, Long courseId) {
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserId, userId)
                .eq(UserCourse::getCourseId, courseId);
        return queryWrapper;
    }

}
