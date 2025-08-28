package com.graduationprojectordermanagementsystem.util;

import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.contents.JwtContent;
import com.graduationprojectordermanagementsystem.exception.TokenExpiredException;
import com.graduationprojectordermanagementsystem.pojo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
@Slf4j
public class JwtUtils {

    private Key signingKey;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 获取签名密钥
     */
    private Key getSigningKey() {
        if (signingKey == null) {
            byte[] keyBytes = Decoders.BASE64.decode(JwtContent.SECRET);
            signingKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return signingKey;
    }

    /**
     * 生成token
     */
    public String generateToken(User user) {

        String username = user.getUsername();
        Date now = new Date();
        Date expiraDate = new Date(now.getTime() + JwtContent.EXPIRE_TIME);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)//添加自定义声明
                .setId(UUID.randomUUID().toString())//添加唯一标识
                .setIssuedAt(now)
                .setExpiration(expiraDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * 从token中提取所有声明
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 设置签名密钥
                    .build()
                    .parseClaimsJws(token) // 验证签名并解析
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT 已过期: {}", token);
            throw e; // 直接抛出，保留类型
        } catch (MalformedJwtException e) {
            log.warn("JWT 格式错误: {}", token);
            throw e;
        } catch (JwtException e) {
            log.warn("JWT 签名无效或解析失败: {}", token);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT 参数非法", e);
            throw e;
        }
    }


    /**
     * 验证 Token 的完整性和有效性
     */
    public Claims validateToken(String token) {

        // 1. 解析并验证JWT
        Claims claims = parseToken(token);
        if (claims == null) {
            throw new JwtException("Token解析失败");
        }

        // 2. 检查黑名单（基于JWT唯一标识jti）
        String jti = claims.getId();
        if (jti != null && redisUtils.isInBlacklist(jti)) { // 带命名空间的键
            throw new TokenExpiredException(CommonContent.TokenExpired);
        }

        // 3. 验证过期时间
        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            throw new ExpiredJwtException(null, claims, "Token已过期");
        }

        return claims;

    }

    /**
     * 从 Token 中获取用户名（假设用户名存储在 "sub" 或自定义字段）
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取 JTI（用于登出时加入黑名单）
     */
    public String getJtiFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getId();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * 获取 Token 剩余有效时间（秒）
     */
    public long getRemainingExpireSeconds(String token) {
        try {
            Claims claims = parseToken(token);
            long remaining = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
            return Math.max(0, remaining);
        } catch (Exception e) {
            return 0;
        }
    }


}
