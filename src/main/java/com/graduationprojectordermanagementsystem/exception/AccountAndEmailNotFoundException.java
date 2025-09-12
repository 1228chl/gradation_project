package com.graduationprojectordermanagementsystem.exception;

import com.graduationprojectordermanagementsystem.result.ResultCode;

/**
 * 账号和邮箱都未找到异常
 */
public class AccountAndEmailNotFoundException extends BaseException {
    public AccountAndEmailNotFoundException(String message) {
        super(ResultCode.VALIDATE_FAILED,message);
    }
}
