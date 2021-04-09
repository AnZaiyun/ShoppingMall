package com.anzaiyun.shoppingmall.authserver.service;

import com.anzaiyun.shoppingmall.authserver.vo.UserLoginVo;

import java.util.Map;

public interface LoginService {
    Map<String, String> checkUserLogin(UserLoginVo userLoginVo);
}
