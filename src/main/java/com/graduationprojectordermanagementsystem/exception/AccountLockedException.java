package com.graduationprojectordermanagementsystem.exception;

import com.graduationprojectordermanagementsystem.result.ResultCode;

/**
 * 账号被锁定异常
 */
public class AccountLockedException extends BaseException {

    public AccountLockedException(String msg) {
        super(ResultCode.FORBIDDEN.getCode(), msg); // 使用 403 状态码
    }

}
