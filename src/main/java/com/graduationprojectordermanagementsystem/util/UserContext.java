package com.graduationprojectordermanagementsystem.util;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> roleHolder = new ThreadLocal<>();
    private static final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    // 设置用户名
    public static void setUsername(String username) {
        usernameHolder.set(username);
    }

    // 获取用户名
    public static String getUsername() {
        return usernameHolder.get();
    }

    // 设置角色
    public static void setRole(String role) {
        roleHolder.set(role);
    }

    // 获取角色
    public static String getRole() {
        return roleHolder.get();
    }

    // 设置用户id
    public static void setUserId(Long userId) {userIdHolder.set(userId);}

    // 获取用户id
    public static Long getUserId() {return userIdHolder.get();}

    // 清除用户名
    public static void clear() {
        usernameHolder.remove();
        roleHolder.remove();
        userIdHolder.remove();
    }


}
