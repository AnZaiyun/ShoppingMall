package com.anzaiyun.shoppingmall.product.controller.web;

import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;
import com.anzaiyun.shoppingmall.product.service.CategoryService;
import com.anzaiyun.shoppingmall.product.vo.Catalog2JsonVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","/index","/index.html"})
    public String indexPage(Model model){
        Long catLevel = 1L;
        List<CategoryEntity> categoryEntityList = categoryService.getCategoryByLevel(catLevel);
        model.addAttribute("categorys",categoryEntityList);
        return "index";
    }

    //index/json/catalog.json
    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Map<String, List<Catalog2JsonVo>> getCatalogJson(){
        Map<String, List<Catalog2JsonVo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }
}
