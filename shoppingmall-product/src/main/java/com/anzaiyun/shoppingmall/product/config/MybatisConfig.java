package com.anzaiyun.shoppingmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement  //开启事务
@MapperScan("com.anzaiyun.shoppingmall.product.dao")
public class MybatisConfig {

    //引入分页插件
    @Bean
    public PaginationInterceptor paginationInnerInterceptor(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();

        //设置请求的页面大于最大页，是否继续请求，true转为请求首页，false继续请求，默认false
        paginationInterceptor.setOverflow(true);
        //设置单页的最大数量，默认500，-1不限制
        paginationInterceptor.setLimit(50L);
        return paginationInterceptor;
    }
}
