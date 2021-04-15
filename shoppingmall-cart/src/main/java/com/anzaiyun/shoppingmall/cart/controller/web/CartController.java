package com.anzaiyun.shoppingmall.cart.controller.web;

import com.anzaiyun.common.vo.UserLoginVo;
import com.anzaiyun.shoppingmall.cart.service.CartService;
import com.anzaiyun.shoppingmall.cart.vo.Cart;
import com.anzaiyun.shoppingmall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @RequestMapping("/cartList")
    public String cartList(Model model, HttpSession session){
        //判断当前用户是否登录，如果已登录，则将redis中的购物车数据和数据库中的购物车数据合并，如果没登录则只获取redis中的购物车数据
        UserLoginVo userInfo = (UserLoginVo) session.getAttribute("userInfo");
        String sessionId = (String) model.getAttribute("sessionId");
        Cart cart = new Cart();
        if (userInfo==null){
            cart = cartService.getCartByIdFromRedis(sessionId);
        }else{
            cart = cartService.getCartByIdFromDB(sessionId);
        }

        return "cartList";
    }

    @RequestMapping("/success")
    public String success(Model model){
        String sessionId = (String) model.getAttribute("sessionId");
        CartItem cartItem = new CartItem();
        cartService.addCartItem(sessionId, cartItem);

        return "success";
    }
}
