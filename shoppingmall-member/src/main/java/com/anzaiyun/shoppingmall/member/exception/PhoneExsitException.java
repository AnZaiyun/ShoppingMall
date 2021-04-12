package com.anzaiyun.shoppingmall.member.exception;

public class PhoneExsitException extends RuntimeException{

    public PhoneExsitException() {
        super("当前手机号已注册");
    }
}
