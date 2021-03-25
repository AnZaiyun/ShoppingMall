package com.anzaiyun.shoppingmall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
public class ProductRedisCacheConfig {

    /**
     * 使用EnableConfigurationProperties注解，在该类中可以自动注入CacheProperties类
     * @param cacheProperties
     * @return
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties){

        //获取默认的CacheConfig
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));

        //如果实现了rediscache的配置类，RedisCacheConfiguration将不会再加载配置文件中配置信息，所以这里需要将配置文件的
        //处理步骤再拷贝一份
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }
}
