package com.anzaiyun.shoppingmall.member.exception;

public class UserNameExsitException extends RuntimeException{

    public UserNameExsitException() {
        super("当前用户名已存在");
    }
}
