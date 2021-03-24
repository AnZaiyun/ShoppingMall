package com.anzaiyun.shoppingmall.product;

//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClient;
//import com.aliyun.oss.OSSClientBuilder;
import com.anzaiyun.shoppingmall.product.config.ProductRedissonConfig;
import com.anzaiyun.shoppingmall.product.entity.BrandEntity;
import com.anzaiyun.shoppingmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@SpringBootTest
class ShoppingmallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductRedissonConfig productRedissonConfig;

//    @Autowired
//    OSSClient ossClient;
    //测试阿里云存储功能
    @Test
    void testUpload() throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI4Fzqdnk1guSMpuokSQZ5";
//        String accessKeySecret = "tDNe03mZBo8GjIA4ZDSHiGFoHntsay";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\hspcadmin\\Desktop\\oss_jpg.png");
//        ossClient.putObject("an-shopping-mall", "oss_jpg", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
    }

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("");
        brandEntity.setName("huawei");
        brandService.save(brandEntity);
        System.out.println("保存成功。。。");
    }

    @Test
    void testStringRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","world_"+ UUID.randomUUID().toString());

        String hello = ops.get("hello");
        System.out.println(hello);
    }

    @Test
    void testRedisson() throws IOException {
        RedissonClient redisson = productRedissonConfig.redisson();
        System.out.println(redisson);
    }
}
