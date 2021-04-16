package com.anzaiyun.shoppingmall.cart.service.impl;

import com.anzaiyun.shoppingmall.cart.fegin.ProductFeignService;
import com.anzaiyun.shoppingmall.cart.interceptor.CartInterceptor;
import com.anzaiyun.shoppingmall.cart.service.CartService;
import com.anzaiyun.shoppingmall.cart.vo.CartItem;
import com.anzaiyun.shoppingmall.cart.vo.SkuInfoVo;
import com.anzaiyun.shoppingmall.cart.vo.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

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

        UserStatus userStatus = CartInterceptor.threadLocal.get();
        String cartKey = "shoppingmall:cart:";
        if (userStatus!=null){
            cartKey = cartKey + userStatus.getUserId();
        }else {
            cartKey = cartKey + userStatus.getUserId();
        }

        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey);

        CartItem item = new CartItem();

        CompletableFuture<Void> itemFuture = CompletableFuture.runAsync(() -> {
            // 更新商品信息
            SkuInfoVo skuInfoVo = productFeignService.getInfo(skuId);
            if (skuInfoVo != null) {
                item.setCount(num);
                item.setImage(skuInfoVo.getSkuDefaultImg());
                item.setPrice(skuInfoVo.getPrice());
                item.setSkuId(skuId);
                item.setTitle(skuInfoVo.getSkuTitle());
            }
        }, executor);


        CompletableFuture<Void> attrFuture = CompletableFuture.runAsync(() -> {
            // 更新商品的销售属性信息
            List<String> saleAttrs = productFeignService.getSaleAttrAsStringList(skuId);
            item.setSkuAttr(saleAttrs);
        }, executor);

        CompletableFuture.allOf(itemFuture,attrFuture).get();

        ops.put(skuId,item);

        return item;
    }
}
