package com.anzaiyun.shoppingmall.thirdparty;

import com.anzaiyun.shoppingmall.thirdparty.component.SmsComponent;
import com.anzaiyun.shoppingmall.thirdparty.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ShoppingmallThirdPartyApplicationTests {

    @Autowired
    SmsComponent smsComponent;

    @Test
    void contextLoads() {
    }

    @Test
    void readCsvFile(){
        File csvFile = new File("classpath:\\resources\\AccessKey.csv");
        String[] key = new String[2];
        String inString = "";
        try {

            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            while ((inString = reader.readLine())!=null){
                key = inString.split(",");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(key[0]+key[1]);

    }

    @Test
    public void test(){
        smsComponent.sendSmsCode("18226617983","9999");
    }

    @Test
    public void testSendMessage(){
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "b2cf6fee26b749c1b62428d3ff6909d2";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "18226617983");
        querys.put("param", "**code**:7758521,**minute**:5");
        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
