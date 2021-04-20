package com.anzaiyun.shoppingmall.product.test;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RabbitListener(queues = {"TestDirectQueue"})
public class DirectReceiver {

    /**
     * 返回值需要为空，不然会因为没有指定确认函数，而导致报错
     * org.springframework.amqp.rabbit.listener.adapter.ReplyFailureException: Failed to send reply with payload
     * @param testMessage
     */
    @RabbitHandler
    public void receiveDirectMessage(Map testMessage){
        System.out.println("receive messgae(1):"+testMessage.toString());
    }
}
