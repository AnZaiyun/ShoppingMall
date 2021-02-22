package com.anzaiyun.shoppingmall.thirdparty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class ShoppingmallThirdPartyApplicationTests {

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

}
