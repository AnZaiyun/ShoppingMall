package com.anzaiyun.shoppingmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.anzaiyun.shoppingmall.member.dao")
@SpringBootApplication
public class ShoppingmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallMemberApplication.class, args);
    }

}
