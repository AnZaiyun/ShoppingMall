package com.anzaiyun.shoppingmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.anzaiyun.shoppingmall.order.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class ShoppingmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallOrderApplication.class, args);
    }

}
