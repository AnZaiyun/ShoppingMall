package com.anzaiyun.shoppingmall.cart.controller.test;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class RabbitTestController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送点对点消息
     * @return
     */
    @GetMapping("/sendDirectMessage")
    public String sendDirectMessage(){
        String messageId = UUID.randomUUID().toString();
        String messageData = "this is DirectMessage,test";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        //将消息携带绑定键值：TestDirectRouting(指定路由规则) 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting",map);
        return "message " +messageData+" send success";
    }

    /**
     * 发送主题消息，属于点对点消息，但是这里的点是以主题为单位
     * @return
     */
    @GetMapping("/sendTopicMessage1")
    public String sendTopicMessage1(){
        String messageId = UUID.randomUUID().toString();
        String messageData = "TopicMessage:MAN";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        //将消息携带绑定键值：TestDirectRouting(指定路由规则) 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TopicExchange", "topic.man",map);
        return "message " +messageData+" send success";
    }

    /**
     * 发送主题消息，属于点对点消息，但是这里的点是以主题为单位
     * @return
     */
    @GetMapping("/sendTopicMessage2")
    public String sendTopicMessage2(){
        String messageId = UUID.randomUUID().toString();
        String messageData = "TopicMessage:WOMAN";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        //将消息携带绑定键值：TestDirectRouting(指定路由规则) 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TopicExchange", "topic.woman",map);
        return "message " +messageData+" send success";
    }

    /**
     * 发送扇形消息，对绑定了交换机的所有队列都会接收到该消息，无路由规则
     * @return
     */
    @GetMapping("/sendFanoutMessage")
    public String sendFanoutMessage(){
        String messageId = UUID.randomUUID().toString();
        String messageData = "FanoutMessage:this is FanoutMessage";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        //将消息携带绑定键值：TestDirectRouting(指定路由规则) 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("FanoutExchange", null,map);
        return "message " +messageData+" send success";
    }

    /**
     * 消息推送到server，但是在server里找不到交换机
     * 回调函数ConfirmCallback::false
     * @return
     */
    @GetMapping("/TestMessageAck")
    public String TestMessageAck() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: non-existent-exchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("non-existent-exchange", "TestDirectRouting", map);
        return "ok";
    }

    /**
     * 消息推送到server，找到交换机了，但是没找到队列
     * 回调函数ConfirmCallback::true,ReturnCallback::false
     * @return
     */
    @GetMapping("/TestMessageAck2")
    public String TestMessageAck2() {
        String messageId = String.valueOf(UUID.randomUUID());
        String messageData = "message: lonelyDirectExchange test message ";
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("messageData", messageData);
        map.put("createTime", createTime);
        rabbitTemplate.convertAndSend("lonelyDirectExchange", "TestDirectRouting", map);
        return "ok";
    }


}
