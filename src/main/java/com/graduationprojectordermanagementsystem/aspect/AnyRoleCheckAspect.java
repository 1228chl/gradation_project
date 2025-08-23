package com.graduationprojectordermanagementsystem.aspect;

import com.graduationprojectordermanagementsystem.annotation.RequireAnyRole;
import com.graduationprojectordermanagementsystem.exception.AccessDeniedException;
import com.graduationprojectordermanagementsystem.util.UserContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Aspect
@Component
public class AnyRoleCheckAspect {


    @Pointcut("@annotation(requireAnyRole)")// 修正切入点表达式以绑定注解参数
    public void requireAnyRolePointcut(RequireAnyRole requireAnyRole) {}

    @Before(value = "requireAnyRolePointcut(requireAnyRole)", argNames = "requireAnyRole")
    public void checkRole(RequireAnyRole requireAnyRole) throws IOException{
        String[] allowedRoles = requireAnyRole.value();// 获取注解中指定的角色
        String userRole = UserContext.getRole();// 获取当前用户角色

        boolean hasAccess = false;// 判断当前用户角色是否允许访问

        if (userRole != null) {// 判断当前用户角色是否为空
            for (String role : allowedRoles) {// 遍历允许访问的角色
                if (role.equals(userRole)) {// 判断当前用户角色是否与允许访问的角色一致
                    hasAccess = true;// 设置当前用户角色允许访问
                    break;// 跳出循环
                }
            }
        }
        if (!hasAccess) {// 判断当前用户角色是否允许访问
            throw new AccessDeniedException("权限不足");
        }
    }
}
