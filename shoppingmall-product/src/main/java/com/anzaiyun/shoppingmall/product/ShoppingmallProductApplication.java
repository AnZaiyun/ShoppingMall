package com.anzaiyun.shoppingmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 整合mybatis-plus
 * 1、导入依赖
 * 2、填写配置
 */
@MapperScan("com.anzaiyun.shoppingmall.product.dao")
@SpringBootApplication
public class ShoppingmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallProductApplication.class, args);
    }

}
