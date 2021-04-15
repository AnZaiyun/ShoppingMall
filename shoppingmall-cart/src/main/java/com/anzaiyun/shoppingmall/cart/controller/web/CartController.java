package com.anzaiyun.shoppingmall.cart.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartController {

    @RequestMapping("/cartList")
    public String cartList(){
        return "cartList";
    }

    @RequestMapping("/success")
    public String success(){
        return "success";
    }
}
