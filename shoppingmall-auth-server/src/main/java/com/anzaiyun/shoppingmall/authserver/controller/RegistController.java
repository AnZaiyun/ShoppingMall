package com.anzaiyun.shoppingmall.authserver.controller;

import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.authserver.fegin.SmsFeginService;
import com.anzaiyun.shoppingmall.authserver.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class RegistController {

    @Autowired
    SmsFeginService smsFeginService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/regist.html")
    public String regist(){
        return "regist";
    }

    /**
     * 注册页提交信息，进行注册
     * @return
     */
    @PostMapping("/regist")
    public String registSubmit(@Validated UserRegistVo userRegist, BindingResult result, Model model){
        if (result.hasErrors()){

            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            model.addAttribute("errors",errors);
            //如果存在校验出错，转发到注册页
            return "forward:/regist";
        }

        //真正注册，调用远程服务进行注册


        return "redirect:/login.html";
    }

    /**
     * 注册页发送验证码
     * @param phone
     * @return
     */
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
