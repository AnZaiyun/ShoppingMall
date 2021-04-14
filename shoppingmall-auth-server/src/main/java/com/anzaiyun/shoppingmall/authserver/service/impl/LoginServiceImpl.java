package com.anzaiyun.shoppingmall.authserver.service.impl;

import com.anzaiyun.shoppingmall.authserver.fegin.MemberFeginService;
import com.anzaiyun.shoppingmall.authserver.service.LoginService;
import com.anzaiyun.common.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    MemberFeginService memberFeginService;

    @Override
    public Map<String, String> checkUserLogin(UserLoginVo userLoginVo) {

        Map<String, String> errors = memberFeginService.beforeLogin(userLoginVo);

        return errors;
    }
}
