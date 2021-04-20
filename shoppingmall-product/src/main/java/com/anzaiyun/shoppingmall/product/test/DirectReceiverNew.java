package com.anzaiyun.shoppingmall.product.test;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RabbitListener(queues = {"TestDirectQueue"})
public class DirectReceiverNew {

    @RabbitHandler
    public void receiveDirectMessage(Map testMessage){
        System.out.println("receive messgae(2):"+testMessage.toString());
    }
}
