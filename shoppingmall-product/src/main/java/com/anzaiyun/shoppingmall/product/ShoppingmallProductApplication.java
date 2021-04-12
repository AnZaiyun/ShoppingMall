package com.anzaiyun.shoppingmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 1、整合MyBatis-Plus
 *      1）、导入依赖
 *      <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.2.0</version>
 *      </dependency>
 *      2）、配置
 *          1、配置数据源；
 *              1）、导入数据库的驱动。https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-versions.html
 *              2）、在application.yml配置数据源相关信息
 *          2、配置MyBatis-Plus；
 *              1）、使用@MapperScan
 *              2）、告诉MyBatis-Plus，sql映射文件位置
 *
 * 2、逻辑删除
 *  1）、配置全局的逻辑删除规则（省略）
 *  2）、配置逻辑删除的组件Bean（省略）
 *  3）、给Bean加上逻辑删除注解@TableLogic
 *
 * 3、JSR303
 *   1）、给Bean添加校验注解:javax.validation.constraints，并定义自己的message提示
 *   2)、开启校验功能@Valid
 *      效果：校验错误以后会有默认的响应；
 *   3）、给校验的bean后紧跟一个BindingResult，就可以获取到校验的结果
 *   4）、分组校验（多场景的复杂校验）
 *         1)、	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
 *          给校验注解标注什么情况需要进行校验
 *         2）、@Validated({AddGroup.class})
 *         3)、默认没有指定分组的校验注解@NotBlank，在分组校验情况@Validated({AddGroup.class})下不生效，只会在@Validated生效；
 *
 *   5）、自定义校验
 *      1）、编写一个自定义的校验注解
 *      2）、编写一个自定义的校验器 ConstraintValidator
 *      3）、关联自定义的校验器和自定义的校验注解
 *      @Documented
 * @Constraint(validatedBy = { ListValueConstraintValidator.class【可以指定多个不同的校验器，适配不同类型的校验】 })
 * @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
 * @Retention(RUNTIME)
 * public @interface ListValue {
 *
 * 4、统一的异常处理
 * @ControllerAdvice
 *  1）、编写异常处理类，使用@ControllerAdvice。
 *  2）、使用@ExceptionHandler标注方法可以处理的异常。
 *
 * 5、远程调用服务，fegin
 * 1）引入依赖
 * <dependency>
 *   <groupId>org.springframework.cloud</groupId>
 *   <artifactId>spring-cloud-starter-openfeign</artifactId>
 * </dependency>
 * 2）主启动类添加注解
 * @EnableFeignClients(basePackages = "com.anzaiyun.shoppingmall.product.fegin")
 * 其中basePackages为fegin接口的相对路径
 * 3）编写fegin接口
 * @FeignClient("shoppingmall-search")
 * public interface SearchFeginService {
 *
 *     @PostMapping("/search/product/upproduct")
 *     public R upProduct(@RequestBody List<SkuEsModelTo> skuEsModelList);
 *
 * }
 * 其中FeignClient中value为要远程调用的服务在nacos中的名字
 * PostMapping为服务链接的完整路径，类映射路径+方法映射路径
 * 具体的方法为远程controller的完全拷贝，两边的方法签名完全一致
 * 其中因为网络间传递的为json字符串，所以如果需要转为实体类，需要添加@RequestBody注解
 *
 * 6、nginx配置
 * 访问shoppingmall.com,nginx会拦截请求，根据路由规则，静态文件直接返回，动态数据则转发到服务端网关，网关再根据路由规则转发
 * 到具体的服务
 *
 * 7、thymeleaf模板引擎
 * 1）引入依赖
 * <dependency>
 *    <groupId>org.springframework.boot</groupId>
 *    <artifactId>spring-boot-starter-thymeleaf</artifactId>
 * </dependency>
 *
 * 其中为了方便开发，每次修改模板文件后不需要重启项目，可以引入devtools，这样修改文件后只需要重新编译项目即可（快捷键ctrl+F9）
 * 需要注意的是如果修改了配置文件，则还是要重启项目
 * <dependency>
 *    <groupId>org.springframework.boot</groupId>
 *    <artifactId>spring-boot-devtools</artifactId>
 *    <optional>true</optional>
 * </dependency>
 */
@EnableCaching
@MapperScan("com.anzaiyun.shoppingmall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.anzaiyun.shoppingmall.product.fegin")
public class ShoppingmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingmallProductApplication.class, args);
    }

}
