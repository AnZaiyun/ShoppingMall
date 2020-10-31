package com.anzaiyun.shoppingmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、开启服务注册发现
 */
@EnableDiscoveryClient
//暂不启用数据源功能
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ShoppingmallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallGatewayApplication.class, args);
    }

}
