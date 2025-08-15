package com.graduationprojectordermanagementsystem.util;

import com.graduationprojectordermanagementsystem.contents.CommonContent;
import com.graduationprojectordermanagementsystem.contents.JwtContent;
import com.graduationprojectordermanagementsystem.exception.TokenExpiredException;
import com.graduationprojectordermanagementsystem.pojo.dto.LoginDTO;
import com.graduationprojectordermanagementsystem.pojo.vo.LoginVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtUtils {
    @Resource
    private RedisUtils redisUtils;


    /**
     * 生成token
     */
    public String generateToken(LoginDTO loginDTO) {

        String username = loginDTO.getUsername();
        Date now = new Date();
        Date expiraDate = new Date(now.getTime() + JwtContent.EXPIRE_TIME);
        UUID uuid = UUID.randomUUID();

        return Jwts.builder()
                .setSubject(username)
                .setId(uuid.toString())
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
            throw new RuntimeException("令牌已过期", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("令牌格式错误", e);
        } catch (JwtException e) {
            throw new RuntimeException("签名验证失败", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("令牌参数非法", e);
        }
    }


    /**
     * 验证 Token 的完整性和有效性
     */
    public Boolean validateToken(String token) {

        // 1. 解析并验证JWT
        Claims claims = parseToken(token);
        if (claims == null) {
            throw new JwtException("Token解析失败");
        }

        // 2. 检查黑名单（基于JWT唯一标识jti）
        String jti = claims.getId();
        if (redisUtils.isInBlacklist(jti)) { // 带命名空间的键
            throw new TokenExpiredException(CommonContent.TokenExpired);
        }

        // 3. 验证过期时间
        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            throw new ExpiredJwtException(null, claims, "Token已过期");
        }

        return true;

    }

    /**
     * 从 Token 中获取用户名（假设用户名存储在 "sub" 或自定义字段）
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }


    /**
     * 获取签名密钥
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JwtContent.SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
