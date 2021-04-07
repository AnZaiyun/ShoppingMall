package com.anzaiyun.shoppingmall.product.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadConfig {

    @Bean
    public ThreadPoolExecutor createThreadPoolExecutor(ThreadConfigProperties config){
        return new ThreadPoolExecutor(config.getCorePoolSize(),config.getMaximumPoolSize(),
                config.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(500),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
