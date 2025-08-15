package com.graduationprojectordermanagementsystem.exception;

public class AccountAndEmailNotFoundException extends RuntimeException {
    public AccountAndEmailNotFoundException(String message) {
        super(message);
    }
}
