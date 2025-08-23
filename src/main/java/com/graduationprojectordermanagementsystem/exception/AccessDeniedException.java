package com.graduationprojectordermanagementsystem.exception;

/**
 * 权限不足抛出的异常
 */
public class AccessDeniedException extends BaseException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
