package com.anzaiyun.shoppingmall.product.controller.test;

import com.anzaiyun.shoppingmall.product.test.DirectReceiver;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RabbitTestController {

    @Autowired
    DirectReceiver directReceiver;

    @GetMapping("/receiveDirectMessage")
    public String receiveDirectMessage(){
        return null;
    }
}
