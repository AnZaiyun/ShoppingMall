package com.anzaiyun.shoppingmall.authserver.service;

import com.anzaiyun.shoppingmall.authserver.vo.UserRegistVo;

import java.util.Map;

public interface RegistService {
    Map<String, String> checkCode(UserRegistVo userRegist);
}
