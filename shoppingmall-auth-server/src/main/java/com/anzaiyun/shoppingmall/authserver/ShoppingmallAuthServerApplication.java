package com.anzaiyun.shoppingmall.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * spring session 核心原理
 * 1）@EnableRedisHttpSession 导入了 RedisHttpSessionConfiguration，该设置类中注入了RedisIndexedSessionRepository
 * 2）RedisHttpSessionConfiguration 继承了SpringHttpSessionConfiguration，而在SpringHttpSessionConfiguration中注入了一个session的过滤器 SessionRepositoryFilter
 * 3）SessionRepositoryFilter中则增加了一个过滤器
 *   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
 * 			throws ServletException, IOException {
 * 		这一步将redis session作为属性封装到了request中，下面二次封装进了session中
 * 		request.setAttribute(SESSION_REPOSITORY_ATTR, this.sessionRepository);
 *
 * 		SessionRepositoryRequestWrapper wrappedRequest = new SessionRepositoryRequestWrapper(request, response);
 * 		SessionRepositoryResponseWrapper wrappedResponse = new SessionRepositoryResponseWrapper(wrappedRequest,
 * 				response);
 *
 * 		try {
 * 			filterChain.doFilter(wrappedRequest, wrappedResponse);
 *        }
 * 		finally {
 * 			wrappedRequest.commitSession();
 *        }
 *    }
 *  也是在这个过滤器中将原生的session替换成了包装后的session
 *
 */
@EnableFeignClients(basePackages = "com.anzaiyun.shoppingmall.authserver.fegin")
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession
public class ShoppingmallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallAuthServerApplication.class, args);
    }

}
