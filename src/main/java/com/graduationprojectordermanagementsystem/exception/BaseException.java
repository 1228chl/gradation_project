package com.graduationprojectordermanagementsystem.exception;

import com.graduationprojectordermanagementsystem.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常父类
 */
@Getter
public class BaseException extends RuntimeException {
    private final Integer code;

    // 默认错误码 500
    public BaseException(String message) {
        this(ResultCode.ERROR.getCode(), message);
    }

    // 自定义错误码
    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    // 推荐：使用 ResultCode 枚举
    public BaseException(ResultCode resultCode, String message) {
        this(resultCode.getCode(), resultCode.getMsg() + "：" + message);
    }

}
