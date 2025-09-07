package com.graduationprojectordermanagementsystem.contents;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * jwt常量
 */
@Component
@Data
public class JwtContent {
    // 过期时间(秒) -
    @Value("${jwt.expire-time}")
    private Long EXPIRE_TIME;//2小时


    // JWT密钥
    @Value("${jwt.secret}")
    private String SECRET;
}
