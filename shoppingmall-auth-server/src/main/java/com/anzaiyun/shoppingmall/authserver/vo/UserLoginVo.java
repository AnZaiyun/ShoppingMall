package com.anzaiyun.shoppingmall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginVo {
    @NotEmpty(message = "用户名必须填写")
    @Length(min = 4,max = 8,message = "用户名长度最小4位，最长8位")
    private String uName;

    @NotEmpty(message = "用户密码必须填写")
    private String uPwd;
}
