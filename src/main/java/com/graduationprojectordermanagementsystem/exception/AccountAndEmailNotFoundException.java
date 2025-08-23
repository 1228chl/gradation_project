package com.graduationprojectordermanagementsystem.exception;

/**
 * 账号和邮箱都未找到异常
 */
public class AccountAndEmailNotFoundException extends BaseException {
    public AccountAndEmailNotFoundException(String message) {
        super(message);
    }
}
