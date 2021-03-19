package com.anzaiyun.shoppingmall.product.controller.web;

import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;
import com.anzaiyun.shoppingmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","/index","/index.html"})
    public String indexPage(){
        Long catLevel = 1L;
        List<CategoryEntity> categoryEntityList = categoryService.getCategoryByLevel(catLevel);
        return "index";
    }
}
