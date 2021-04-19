package com.anzaiyun.shoppingmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.anzaiyun.shoppingmall.cart.fegin.ProductFeignService;
import com.anzaiyun.shoppingmall.cart.interceptor.CartInterceptor;
import com.anzaiyun.shoppingmall.cart.service.CartService;
import com.anzaiyun.shoppingmall.cart.vo.Cart;
import com.anzaiyun.shoppingmall.cart.vo.CartItem;
import com.anzaiyun.shoppingmall.cart.vo.SkuInfoVo;
import com.anzaiyun.shoppingmall.cart.vo.UserStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public CartItem addToCart(Long skuId, Long num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> ops = getStringObjectObjectBoundHashOperations();

        //在添加商品前需要先判断当前用户购物车中是否已经存在该商品信息，如果已存在则需要更新购物车
        List<Object> values = ops.values();
        CartItem item = new CartItem();
        for (Object value : values) {
            CartItem cartItem = JSON.parseObject(value.toString(), CartItem.class);
            if (cartItem.getSkuId().equals(skuId)){
                item = cartItem;
                break;
            }
        }

        if (item!=null){
            // 当前购物车中已经存在该商品信息
            item.setCount(item.getCount() + num);
        }
        else{
            CartItem finalItem = item;
            CompletableFuture<Void> itemFuture = CompletableFuture.runAsync(() -> {
                // 更新商品信息
                SkuInfoVo skuInfoVo = productFeignService.getInfo(skuId);
                if (skuInfoVo != null) {
                    finalItem.setCount(num);
                    finalItem.setImage(skuInfoVo.getSkuDefaultImg());
                    finalItem.setPrice(skuInfoVo.getPrice());
                    finalItem.setSkuId(skuId);
                    finalItem.setTitle(skuInfoVo.getSkuTitle());
                }
            }, executor);

            CompletableFuture<Void> attrFuture = CompletableFuture.runAsync(() -> {
                // 更新商品的销售属性信息
                List<String> saleAttrs = productFeignService.getSaleAttrAsStringList(skuId);
                finalItem.setSkuAttr(saleAttrs);
            }, executor);

            CompletableFuture.allOf(itemFuture,attrFuture).get();

            item = finalItem;
        }

        ops.put(skuId,item);

        return item;
    }

    /**
     * 方法重载，根据item增加购物车数据
     * @param item
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public CartItem addToCart(CartItem item) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> ops = getStringObjectObjectBoundHashOperations();

        //在添加商品前需要先判断当前用户购物车中是否已经存在该商品信息，如果已存在则需要更新购物车
        List<Object> values = ops.values();
        CartItem tmpItem = null;
        for (Object value : values) {
            CartItem cartItem = JSON.parseObject(value.toString(), CartItem.class);
            if (cartItem.getSkuId().equals(item.getSkuId())){
                tmpItem = cartItem;
                break;
            }
        }

        if (tmpItem!=null){
            // 当前购物车中已经存在该商品信息
            tmpItem.setCount(tmpItem.getCount() + item.getCount());
        }
        else{
            tmpItem = item;
        }

        ops.put(tmpItem.getSkuId(),item);

        return item;
    }

    /**
     * 获取购物车里的所有商品
     * @return
     */
    @Override
    public Cart getCartList() throws ExecutionException, InterruptedException {
        UserStatus userStatus = CartInterceptor.threadLocal.get();
        String cartKey = "shoppingmall:cart:";
        String tmpCartKey = "";
        String loginCartKey = "";
        if (userStatus.getUserId()!=null){
            //登录用户
            loginCartKey = cartKey + userStatus.getUserId();
        }else {
            //临时用户
            tmpCartKey = cartKey + userStatus.getUserKey();
        }

        Cart cartList = new Cart();
        if (StringUtils.isNotEmpty(tmpCartKey) && StringUtils.isNotEmpty(loginCartKey)){
            //临时用户、登录用户均不为空，需要将临时购物车的数据添加到登录后购物车中
            List<CartItem> cartItems = getCartItems(tmpCartKey);
            for (CartItem cartItem : cartItems) {
                // 调用添加购物车重载方法,增加购物车商品
                addToCart(cartItem);
            }
            // 删除临时用户购物车
            redisTemplate.delete(tmpCartKey);

            cartList.setCartItems(getCartItems(loginCartKey));
        }else if (StringUtils.isNotEmpty(tmpCartKey)){
            cartList.setCartItems(getCartItems(tmpCartKey));
        }else if (StringUtils.isNotEmpty(loginCartKey)){
            cartList.setCartItems(getCartItems(loginCartKey));
        }

        return cartList;
    }

    public List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);
        List<Object> values = ops.values();

        return values.stream().map((value) -> {
            CartItem cartItem = JSON.parseObject(value.toString()).toJavaObject(CartItem.class);

            return cartItem;
        }).collect(Collectors.toList());
    }

    public BoundHashOperations<String, Object, Object> getStringObjectObjectBoundHashOperations() {
        UserStatus userStatus = CartInterceptor.threadLocal.get();
        String cartKey = "shoppingmall:cart:";
        if (userStatus.getUserId()!=null){
            //登录用户
            cartKey = cartKey + userStatus.getUserId();
        }else {
            //临时用户
            cartKey = cartKey + userStatus.getUserKey();
        }

        return redisTemplate.boundHashOps(cartKey);
    }
}
