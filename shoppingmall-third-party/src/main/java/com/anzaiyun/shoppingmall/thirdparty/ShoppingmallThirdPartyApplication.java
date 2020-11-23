package com.anzaiyun.shoppingmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ShoppingmallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallThirdPartyApplication.class, args);
    }

}
