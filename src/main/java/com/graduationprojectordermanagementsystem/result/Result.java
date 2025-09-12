package com.graduationprojectordermanagementsystem.result;

import lombok.Data;
import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {
    private Integer code;        // 状态码
    private String msg;          // 描述信息
    private T data;              // 数据
    private Boolean success;     // 是否成功（方便前端判断）

    private Result() {}

    private Result(Integer code, String msg, T data, Boolean success) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = success;
    }

    /**
     * 成功 - 无数据
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null, true);
    }

    /**
     * 成功 - 带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data, true);
    }

    /**
     * 成功 - 自定义消息
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, null, true);
    }

    /**
     * 成功 - 自定义消息 + 数据
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data, true);
    }

    /**
     * 失败 - 默认错误
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg(), null, false);
    }

    /**
     * 失败 - 自定义消息
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ResultCode.ERROR.getCode(), msg, null, false);
    }

    /**
     * 失败 - 自定义状态码 + 消息
     */
    public static <T> Result<T> error(ResultCode resultCode, String msg) {
        return new Result<>(resultCode.getCode(), msg, null, false);
    }

    /**
     * 失败 - 自定义状态码（使用枚举默认 msg）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null, false);
    }

    /**
     * 失败 - 自定义状态码 + 数据（用于带错误详情的场景）
     */
    public static <T> Result<T> error(ResultCode resultCode, String msg, T data) {
        return new Result<>(resultCode.getCode(), msg, data, false);
    }

    // ---------------- 可选：链式调用支持 ----------------

    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public Result<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    public Result<T> success(Boolean success) {
        this.success = success;
        return this;
    }
}
