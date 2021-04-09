package com.anzaiyun.shoppingmall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 使用springMvc 自带的viewcontroller转发功能，直接配置页面跳转功能
 */
@Configuration
public class WebControllerConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //路径映射默认是get方法
        registry.addViewController("/denglu").setViewName("login");
    }
}
