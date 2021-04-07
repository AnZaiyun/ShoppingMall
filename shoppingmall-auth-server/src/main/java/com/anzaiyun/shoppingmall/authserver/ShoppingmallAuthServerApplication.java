package com.anzaiyun.shoppingmall.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.anzaiyun.shoppingmall.authserver.fegin")
@EnableDiscoveryClient
@SpringBootApplication
public class ShoppingmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallAuthServerApplication.class, args);
    }

}