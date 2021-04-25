package com.anzaiyun.shoppingmall.cart.service;

import com.anzaiyun.shoppingmall.cart.vo.Cart;
import com.anzaiyun.shoppingmall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {

    /**
     * 将商品添加到购物车
     * @param skuId
     * @param num
     * @return
     */
    CartItem addToCart(Long skuId, Long num) throws ExecutionException, InterruptedException;

    /**
     * 获取用户所有的购物车数据
     * @return
     */
    Cart getCartList() throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    /**
     * 勾选购物项
     * @param skuId
     * @param isChecked
     */
    void checkCart(Long skuId, Long isChecked);

    void updateItemCount(Long skuId, Long count);
}
