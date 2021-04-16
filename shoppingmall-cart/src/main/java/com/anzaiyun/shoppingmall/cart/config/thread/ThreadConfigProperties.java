package com.anzaiyun.shoppingmall.cart.config.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("shoppingmall.thread")
@Component
@Data
public class ThreadConfigProperties {
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Integer keepAliveTime;
}
