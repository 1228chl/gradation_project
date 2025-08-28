package com.graduationprojectordermanagementsystem.util;

import com.graduationprojectordermanagementsystem.contents.JwtContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 简化版 Redis 工具类（不依赖 Spring Security）
 * 功能：缓存任意对象 + JWT 黑名单管理
 */
@Component
public class RedisUtils {
    private final RedisTemplate<String, Object> redisTemplate;
    //缓存前缀
    private static final String CACHE_PREFIX = "app:cache:";
    //黑名单前缀
    private static final String BLACKLIST_PREFIX = "app:blacklist:";

    @Autowired
    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== 通用缓存操作 ====================
    /**
     * 存入信息（带过期时间）
     * @param key    键
     * @param value  值（支持任意对象）
     */
    public <T> void setCache(String key, T value) {
        Long time = JwtContent.EXPIRE_TIME;// 过期时间（单位：秒）和jwt一致
        TimeUnit unit = TimeUnit.SECONDS;// 时间单位(默认为秒)
        redisTemplate.opsForValue().set(CACHE_PREFIX + key, value, time, unit);
    }

    /**
     * 取出对象（带类型安全检查）
     * @param key 键
     * @param clazz    目标类型
     * @return         转换后的对象或 null
     */
    public <T> T getCache(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(CACHE_PREFIX + key);
        if (clazz.isInstance(value)) {// 类型检查
            return clazz.cast(value);
        }
        return null;
    }

    /**
     * 删除对象
     * @param key 键
     */
    public void deleteCache(String key) {
        redisTemplate.delete(CACHE_PREFIX + key);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean hasCache(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(CACHE_PREFIX + key));
    }

    // ==================== JWT 黑名单操作 ====================
    /**
     * 将 JWT 的JTI 加入黑名单（自动设置 Token 剩余有效期）
     * @param jti           JWT的唯一标识（从 Token 解析）
     * @param expireTimeMs  JWT剩余有效时间（秒）
     */
    public void addToBlacklist(String jti, long expireTimeMs) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + jti,// 带命名空间
                "invalid",// 值随便填
                expireTimeMs,// 过期时间
                TimeUnit.SECONDS// 时间单位
        );
    }

    /**
     * 检查 JTI 是否在黑名单中
     * @param jti JWT的唯一标识
     */
    public boolean isInBlacklist(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));// 判断缓存是否存在
    }

    /**
     * 立即从黑名单移除（可用于管理员强制恢复 Token，一般不用）
     */
    public void removeFromBlacklist(String jti){
        redisTemplate.delete(BLACKLIST_PREFIX + jti);// 删除缓存
    }
}

