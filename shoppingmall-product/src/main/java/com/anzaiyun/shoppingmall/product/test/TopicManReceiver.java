package com.anzaiyun.shoppingmall.product.test;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RabbitListener(queues = "topic.man")
public class TopicManReceiver {

    /**
     * 监听队列topic.man，该队列的路由规则是topic.man，当发送路由规则为topic.man和topic.woman的数据时，该队列只会接收到
     * 路由规则为topic.man的数据
     * @param testMessage
     */
    @RabbitHandler
    public void process(Map testMessage) {
        System.out.println("TopicManReceiver消费者收到消息  : " + testMessage.toString());
    }
}
