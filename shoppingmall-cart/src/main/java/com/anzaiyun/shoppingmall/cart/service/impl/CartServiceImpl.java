package com.anzaiyun.shoppingmall.cart.service.impl;

import com.anzaiyun.shoppingmall.cart.service.CartService;
import com.anzaiyun.shoppingmall.cart.vo.Cart;
import com.anzaiyun.shoppingmall.cart.vo.CartItem;
import org.springframework.stereotype.Service;

@Service("cartService")
public class CartServiceImpl implements CartService {


    @Override
    public Cart getCartByIdFromRedis(String sessionId) {
        return null;
    }

    @Override
    public Cart getCartByIdFromDB(String sessionId) {
        return null;
    }

    @Override
    public void addCartItem(String sessionId, CartItem cartItem) {

    }
}
