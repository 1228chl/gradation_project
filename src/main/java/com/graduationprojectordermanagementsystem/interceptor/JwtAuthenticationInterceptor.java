package com.graduationprojectordermanagementsystem.interceptor;

import com.graduationprojectordermanagementsystem.util.JwtUtils;
import com.graduationprojectordermanagementsystem.util.RedisUtils;
import com.graduationprojectordermanagementsystem.util.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisUtils redisUtils;

    // 可以配置不需要认证的路径（如登录、注册）
    private static final String[] EXCLUDE_PATHS = {
            "/api/user/login",
            "/api/user/register"
//            "/api/public/**"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        log.info("拦截请求: {}", request.getRequestURI());
        log.info("Authorization Header: {}", request.getHeader("Authorization"));
        String requestURI = request.getRequestURI();

        //1. 检查当前请求是否需要认证
        if (isExcludedPath(requestURI)){
            return true;
        }

        //2. 放行 OPTIONS 请求（预检）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;  // 放行
        }
        //3. 获取 Authorization 头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            setUnauthorizedResponse(response, "未提供Token或格式错误");
            return false;
        }

        String token = authHeader.substring(7);//去掉 "Bearer " 前缀

        try{
            // ✅ 先解析 jti（在验证前就可以解析，用于黑名单检查）
            String jti = jwtUtils.getJtiFromToken(token); // 你需要在 JwtUtils 中实现这个方法
            if (jti != null) {
                // ✅ 检查是否在黑名单中
                if (redisUtils.isInBlacklist(jti)) {
                    setUnauthorizedResponse(response, "Token已失效，请重新登录");
                    return false;
                }
            }
            //4.验证 Token并获取 Claims
            Claims claims = jwtUtils.validateToken(token);

            //5.提取用户信息，存入上下文中
            String username = claims.getSubject();
            String role = claims.get("role", String.class);//获取角色
            Integer userId = claims.get("userId", Integer.class);
            if (username == null && role == null){
                setUnauthorizedResponse(response, "Token中缺少必要信息");
                return false;
            }
            UserContext.setUsername(username);//存入当前线程上下文
            UserContext.setRole(role);
            UserContext.setUserId(userId);

            // 6. 可选：将用户ID等信息也存入
            // UserContext.setUserId(...); // 如果 Claims 中有额外信息

            return true;//放行
        } catch (ExpiredJwtException e) {
            setUnauthorizedResponse(response, "Token已过期");
            return false;

        } catch (MalformedJwtException e) {
            setUnauthorizedResponse(response, "Token无效或签名错误");
            return false;

        } catch (Exception e) {
            setUnauthorizedResponse(response, "认证失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 判断路径是否在排除列表中（简单通配符支持）
     */
    private boolean isExcludedPath(String path) {
        for (String exclude : EXCLUDE_PATHS) {
            if (exclude.endsWith("/**")) {
                String prefix = exclude.substring(0, exclude.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(exclude)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 统一返回401响应
     */
    private void setUnauthorizedResponse(HttpServletResponse response,  String msg)throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// 设置响应状态码为401
        response.setContentType("application/json;charset=UTF-8");// 设置响应内容类型为JSON
        response.getWriter().write("{\"code\":401,\"msg\":\"" + msg + "\"}");// 返回错误信息
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler, Exception ex) {
        // 请求结束后清除 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }

}
