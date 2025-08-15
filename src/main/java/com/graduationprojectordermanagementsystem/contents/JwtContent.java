package com.graduationprojectordermanagementsystem.contents;

/**
 * jwt常量
 */
public class JwtContent {
    // 过期时间(秒) - 24小时
    public static final Long EXPIRE_TIME = 60 * 60 * 1000L;//一小时
    //public static final Long EXPIRE_TIME = 24 * 60 * 60L;//24小时
    //public static final Long EXPIRE_TIME = 3 * 24 * 60 * 60L;//3天
    //public static final Long EXPIRE_TIME = 7 * 24 * 60 * 60L;//7天


    // JWT密钥
    public static final String SECRET = "7JWI64WV7ZWY7IS47JqUIOyggOuKlCDsp4DquIggand0IOqzteu2gOykkeyeheuL";

    // Token请求头
    public static final String TOKEN_HEADER = "Authorization";

    // Token前缀
    public static final String TOKEN_PREFIX = "Bearer ";
}
