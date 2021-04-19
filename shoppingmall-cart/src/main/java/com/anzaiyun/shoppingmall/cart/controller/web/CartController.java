package com.anzaiyun.shoppingmall.cart.controller.web;

import com.anzaiyun.shoppingmall.cart.interceptor.CartInterceptor;
import com.anzaiyun.shoppingmall.cart.service.CartService;
import com.anzaiyun.shoppingmall.cart.vo.Cart;
import com.anzaiyun.shoppingmall.cart.vo.CartItem;
import com.anzaiyun.shoppingmall.cart.vo.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @RequestMapping("/cartList")
    public String cartList(Model model, HttpSession session) throws ExecutionException, InterruptedException {
        //判断当前用户是否登录，如果已登录，则将redis中的购物车数据和数据库中的购物车数据合并，如果没登录则只获取redis中的购物车数据
        //判断用户是否登录的逻辑抽取到拦截器中

        Cart cartlist = cartService.getCartList();
        model.addAttribute("cartList",cartlist);

        return "cartList";
    }

    @RequestMapping("/addToCart/{skuId}")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Long num,
                            RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        CartItem item = cartService.addToCart(skuId, num);
        attributes.addAttribute("item", item);

        //将结果重定向，防止当前页面刷新，多次提交数据
        return "redirect:http://cart.shoppingmall.com/addCartItemSucess";
    }

    @RequestMapping("/addCartItemSucess")
    public String addCartItemSucess(){
        return "success";
    }


}
