package com.anzaiyun.shoppingmall.product.controller.web;

import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;
import com.anzaiyun.shoppingmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
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
