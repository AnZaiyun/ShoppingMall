package com.anzaiyun.shoppingmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 远程调用步骤
 * 1）引入openfeign依赖
 * 2）编写接口，告诉springcloud这个接口调用远程服务
 * 3)开启远程调用功能
 */
@MapperScan("com.anzaiyun.shoppingmall.member.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.anzaiyun.shoppingmall.member.feign")
public class ShoppingmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallMemberApplication.class, args);
    }

}
