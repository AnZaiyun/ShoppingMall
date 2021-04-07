package com.anzaiyun.shoppingmall.product.controller.web;

import com.anzaiyun.shoppingmall.product.service.SkuInfoService;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

@Controller
public class ItemContorller {

    @Autowired
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        System.out.println(skuId);

        SkuItemVo skuItem = skuInfoService.getSkuItem(skuId);
        model.addAttribute("item",skuItem);
        return "item";
    }
}
