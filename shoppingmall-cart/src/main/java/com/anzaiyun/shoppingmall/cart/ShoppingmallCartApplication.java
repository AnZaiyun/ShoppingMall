package com.anzaiyun.shoppingmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableDiscoveryClient
@SpringBootApplication
public class ShoppingmallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallCartApplication.class, args);
    }

}
