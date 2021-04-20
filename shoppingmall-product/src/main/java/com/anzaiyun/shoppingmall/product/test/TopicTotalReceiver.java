package com.anzaiyun.shoppingmall.product.test;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RabbitListener(queues = "topic.woman")
public class TopicTotalReceiver {

    /**
     * 监听队列topic.woman，该队列的路由规则是topic.#，当发送路由规则为topic.man和topic.woman的数据时,
     * 两条数据在该队列中都会接收到
     * @param testMessage
     */
    @RabbitHandler
    public void process(Map testMessage) {
        System.out.println("TopicTotalReceiver消费者收到消息  : " + testMessage.toString());
    }
}
