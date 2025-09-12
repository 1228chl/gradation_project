package com.graduationprojectordermanagementsystem.result;


import lombok.Getter;

/**
 * 统一返回结果状态码
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "成功"),
    ERROR(500, "失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    // ✅ 新增：根据 code 获取枚举实例
    public static ResultCode getByCode(Integer code) {
        for (ResultCode value : ResultCode.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ERROR; // 默认返回 ERROR
    }
}
