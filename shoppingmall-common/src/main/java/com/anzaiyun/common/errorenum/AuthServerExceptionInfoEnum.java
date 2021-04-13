package com.anzaiyun.common.errorenum;

public enum AuthServerExceptionInfoEnum {

    /**
     * code为负值，表示需要报错中断操作的信息
     * code为正值，表示需要警告但可以继续操作的信息
     */
    PHONE_EXIST_ERROR(-16000,"当前手机号已注册"),
    USERNAME_EXIST_ERROR(-16001,"当前用户名已存在"),
    PWD_SIMPLE_WARNING(16000,"当前密码过于简单");

    private int code;
    private String msg;

    AuthServerExceptionInfoEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
