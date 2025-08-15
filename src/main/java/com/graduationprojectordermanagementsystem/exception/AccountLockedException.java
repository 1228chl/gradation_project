package com.graduationprojectordermanagementsystem.exception;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends RuntimeException {

    public AccountLockedException() {
    }

    public AccountLockedException(String msg) {
        super(msg);
    }

}
