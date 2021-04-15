package com.anzaiyun.shoppingmall.authserver.service;

import com.anzaiyun.common.vo.UserInfoVo;
import com.anzaiyun.common.vo.UserLoginVo;

import java.util.Map;

public interface LoginService {
    Map<String, String> checkUserLogin(UserLoginVo userLoginVo);

    UserInfoVo getUserInfo(UserLoginVo userLoginVo);
}
