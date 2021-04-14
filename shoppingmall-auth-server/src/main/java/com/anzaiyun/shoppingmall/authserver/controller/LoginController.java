package com.anzaiyun.shoppingmall.authserver.controller;

import com.anzaiyun.shoppingmall.authserver.service.LoginService;
import com.anzaiyun.common.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    LoginService loginService;

    @GetMapping({"/","/login.html"})
    public String login(HttpSession session){

        Object userInfo = session.getAttribute("userInfo");
        //如果登陆过则直接跳转到首页
        if (userInfo != null){
            return "redirect:http://shoppingmall.com";
        }

        return "login";
    }

    @PostMapping("/loginSubmit")
    public String loginSubmit(@Validated UserLoginVo userLoginVo, BindingResult result,
                              Model model, RedirectAttributes attributes,
                              HttpSession session){

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
        /**
         * https://blog.csdn.net/qq_43371556/article/details/100862785
         * 分布式下web-server（tomcat）原生的session存在一下问题
         * 1、不同服务（不同域名），session不能共享
         * 解决方法：对子域名session进行升级，将session的作用域扩大到所有服务的父域名中
         * 原生的web-server（tomcat）在向cookie中添加信息时，会默认的指定作用域为当前域
         *
         * 2、同一域名的多个服务，session不同步
         * 解决办法：
         * 1）多服务session复制，tomcat原生支持，但是不推荐使用，当多用户且服务较多时，每个服务都需要保存所有的完整的
         * 用户登录信息，而session都是存在服务器的内存中的，这样对于内存的压力较大，不做推荐
         * 2）将session中的信息全部保存到客户端本地的cookie中，客户端每次访问都会携带全部的cookie向服务器请求验证
         * 但是因为敏感信息大多数都是存在session中，且cookie是可任意修改的，如果保存到cookie会造成严重的数据泄露安全隐患
         * 3）采用ip hash一致性，正常情况下每个客户端发出请求的ip地址都是固定的，所以可以对ip地址求hash，nacos做负载均衡时
         * 都将相同hash的ip指定到同一服务
         * 当服务水平扩展时，hash可能会改变，这时候根据hash找服务就会不准确，但是一般而言服务扩展就代表服务进行了维护，原本的
         * session本来就应该失效，而且session本来也是存在有效期的
         * 4）将session信息存在redis中，每次取session时，改为从redis取数，这样多个服务相当于共享了一个session
         * 这个方法实现需要修改原生session的get和set方法，修改量大，所以这里可以引入springsession功能，
         * springsession实现了以上逻辑
         */
        //开启了springsession后，原生session的set和get实际上已被重载??
        session.setAttribute("userInfo",userLoginVo);
        return "redirect:http://shoppingmall.com";
    }
}
