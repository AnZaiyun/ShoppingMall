package com.anzaiyun.shoppingmall.authserver.controller;

import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.authserver.fegin.SmsFeginService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

    @Autowired
    SmsFeginService smsFeginService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/","/login.html"})
    public String login(){
        return "login";
    }

    @ResponseBody
    @GetMapping({"/sendCode","/login.html/sendCode"})
    public R sendCode(@RequestParam("phone") String phone){

        //1、接口防刷


        String redisCode = redisTemplate.opsForValue().get("sms:code:" + phone);
        if(StringUtils.isNotEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            long delayMs = 60 * 1000; //延迟时间
            if (System.currentTimeMillis() - l < delayMs) {
                return R.error(10002, "验证码发送频率过快，请稍后重试");
            }
        }

        //2、配置验证码有效期，以及验证码的再次校验，
        String code = UUID.randomUUID().toString().substring(0, 5);
        //将验证码数据存储到redis
        redisTemplate.opsForValue().set("sms:code:"+phone,code+"_"+System.currentTimeMillis(),10, TimeUnit.MINUTES);
        smsFeginService.sendCode(phone,code);
        return R.ok();
    }
}
