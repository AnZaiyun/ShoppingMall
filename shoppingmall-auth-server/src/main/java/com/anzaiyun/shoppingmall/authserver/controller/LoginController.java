package com.anzaiyun.shoppingmall.authserver.controller;

import com.anzaiyun.shoppingmall.authserver.service.LoginService;
import com.anzaiyun.shoppingmall.authserver.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping({"/","/login.html"})
    public String login(){
        return "login";
    }

    @PostMapping("/loginSubmit")
    public String loginSubmit(@Validated UserLoginVo userLoginVo, Model model){

        //调用远程服务校验用户名，密码信息是否正确
        Map<String,String> errors = loginService.checkUserLogin(userLoginVo);
        if(errors!=null && errors.size()>0){
            model.addAttribute("errors", errors);
            return "login";
        }

        //需要保存用户登录信息

        return "redirect:http://shoppingmall.com";
    }
}
