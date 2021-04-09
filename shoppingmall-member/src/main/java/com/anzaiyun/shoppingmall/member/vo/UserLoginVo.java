package com.anzaiyun.shoppingmall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginVo {

    private String uName;
    private String uPwd;
}
