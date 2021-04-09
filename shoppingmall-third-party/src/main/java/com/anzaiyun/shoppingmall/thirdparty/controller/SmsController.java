package com.anzaiyun.shoppingmall.thirdparty.controller;

import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    SmsComponent smsComponent;

    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsComponent.sendSmsCode(phone, code);
        return R.ok();
    }
}
