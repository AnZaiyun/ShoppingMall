package com.anzaiyun.shoppingmall.authserver.controller;

import com.anzaiyun.shoppingmall.authserver.service.LoginService;
import com.anzaiyun.shoppingmall.authserver.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping({"/","/login.html"})
    public String login(){
        return "login";
    }

    @PostMapping("/loginSubmit")
    public String loginSubmit(@Validated UserLoginVo userLoginVo, BindingResult result, Model model, RedirectAttributes attributes){

        /**
         * jres3.0校验
         */
        if (result.hasErrors()){
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            model.addAttribute("errors",errors);
            attributes.addFlashAttribute("errors",errors);
            //如果存在校验出错，转发到注册页
//            return "regist.html";
            return "redirect:http://auth.shoppingmall.com";
        }

        //调用远程服务校验用户名，密码信息是否正确
        Map<String,String> errors = loginService.checkUserLogin(userLoginVo);
        if(errors!=null && errors.size()>0){
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.shoppingmall.com";
        }

        //TODO 登陆成功后需要保存用户登录信息，在首页展示

        return "redirect:http://shoppingmall.com";
    }
}
