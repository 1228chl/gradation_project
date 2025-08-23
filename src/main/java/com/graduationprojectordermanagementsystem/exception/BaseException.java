package com.graduationprojectordermanagementsystem.exception;

/**
 * 业务异常父类
 */
public class BaseException extends RuntimeException {
    public BaseException() {
    }
    public BaseException(String message) {
        super(message);
    }
}
