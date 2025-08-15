package com.graduationprojectordermanagementsystem.util;

import com.graduationprojectordermanagementsystem.contents.JwtContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "user:auth:";

    @Autowired
    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== 基础操作 ====================
    /**
     * 存入信息（带过期时间）
     * @param username    键
     * @param value  值（支持任意对象）
     */
    public <T> void setCache(String username, T value) {
        String key = KEY_PREFIX + username;
        Long time = JwtContent.EXPIRE_TIME;// 过期时间（单位：秒）和jwt一致
        TimeUnit unit = TimeUnit.SECONDS;// 时间单位(默认为秒)
        redisTemplate.opsForValue().set(key, value, time, unit);
    }

    /**
     * 取出信息（带类型安全检查）
     * @param username 键
     * @param clazz    目标类型
     * @return         转换后的对象或 null
     */
    public <T> T getCache(String username, Class<T> clazz) {
        String key = KEY_PREFIX + username;
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null && clazz.isAssignableFrom(value.getClass())) {
            return clazz.cast(value);
        }
        return null;
    }

    /**
     * 删除信息
     * @param username 键
     */
    public void deleteCache(String username) {
        String key = KEY_PREFIX + username;
        redisTemplate.delete(key);
    }

    /**
     * 验证 key 是否存在
     */
    public boolean existsCache(String username) {
        if (username.startsWith("blacklist:")) {
            return Boolean.TRUE.equals(redisTemplate.hasKey(username));
        }
        String key = KEY_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // ==================== 黑名单专用 ====================
    /**
     * 将 JWT 加入黑名单（自动继承原始 Token 剩余有效期）
     * @param jti           JWT的唯一标识（从 Token 解析）
     * @param expireTimeMs  JWT剩余有效时间（秒）
     */
    public void addToBlacklist(String jti, long expireTimeMs) {
        redisTemplate.opsForValue().set(
                "blacklist:" + jti,
                "1",
                expireTimeMs,
                TimeUnit.SECONDS
        );
    }

    /**
     * 检查 Token 是否在黑名单中
     * @param jti JWT的唯一标识
     */
    public boolean isInBlacklist(String jti) {
        return existsCache("blacklist:" + jti);
    }
}

