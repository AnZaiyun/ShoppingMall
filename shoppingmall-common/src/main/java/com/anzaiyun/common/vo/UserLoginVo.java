package com.anzaiyun.common.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class UserLoginVo implements Serializable {
    @NotEmpty(message = "邮箱/用户名/手机号必须填写")
    private String uName;

    @NotEmpty(message = "用户密码必须填写")
    private String uPwd;
}
