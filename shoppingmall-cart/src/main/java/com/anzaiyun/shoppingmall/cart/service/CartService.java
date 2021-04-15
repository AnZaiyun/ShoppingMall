package com.anzaiyun.shoppingmall.cart.service;

import com.anzaiyun.shoppingmall.cart.vo.Cart;
import com.anzaiyun.shoppingmall.cart.vo.CartItem;

public interface CartService {
    /**
     * 通过唯一标识符sessionid来获取不同的购物车数据
     * @param sessionId
     * @return
     */
    Cart getCartByIdFromRedis(String sessionId);

    Cart getCartByIdFromDB(String sessionId);

    void addCartItem(String sessionId, CartItem cartItem);
}
