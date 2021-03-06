package com.anzaiyun.shoppingmall.authserver.service.impl;

import com.anzaiyun.shoppingmall.authserver.fegin.MemberFeginService;
import com.anzaiyun.shoppingmall.authserver.service.RegistService;
import com.anzaiyun.shoppingmall.authserver.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("registService")
public class RegistServiceImpl implements RegistService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeginService memberFeginService;

    @Override
    public Map<String, String> checkUserRegist(UserRegistVo userRegist) {
        Map<String, String> errors = new HashMap<>();

        //校验验证码
        String code = userRegist.getCode();
        String uPhone = userRegist.getUPhone();
        String redisCode = redisTemplate.opsForValue().get("sms:code:" + uPhone);
        if(StringUtils.isEmpty(redisCode)){
            errors.put("code","验证码已过期，请重新获取");
        }else if(!code.equals(redisCode.split("_")[0])){
            errors.put("code","验证码不正确请重新填写");
        }else {
            //验证码校验通过，需要将redis中的验证码删除
            redisTemplate.delete("sms:code:" + uPhone);
        }

        //校验用户名、手机号是否重复
        Map<String, String> beforeRegist = memberFeginService.beforeRegist(userRegist);
        if (beforeRegist!=null && beforeRegist.size()>0){
            errors.putAll(beforeRegist);
        }

        return errors;
    }
}
