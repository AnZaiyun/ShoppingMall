package com.anzaiyun.shoppingmall.search.controller;

import com.anzaiyun.common.to.es.SkuEsModelTo;
import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.search.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("search/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/upproduct")
    public R upProduct(@RequestBody List<SkuEsModelTo> skuEsModelList){

        try {
            productService.productUp(skuEsModelList);
            return R.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }

    };
}
