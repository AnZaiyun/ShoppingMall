package com.anzaiyun.shoppingmall.authserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistController {

    @GetMapping("/regist.html")
    public String regist(){
        return "regist";
    }
}
