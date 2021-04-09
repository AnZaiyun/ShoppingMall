package com.anzaiyun.shoppingmall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ProductRedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        // 配置redisson锁的保存位置
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");

        return Redisson.create(config);
    }
}
