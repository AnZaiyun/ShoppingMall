package com.anzaiyun.shoppingmall.order;

import com.anzaiyun.shoppingmall.order.entity.OrderOperateHistoryEntity;
import com.anzaiyun.shoppingmall.order.service.OrderOperateHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingmallOrderApplicationTests {

    @Autowired
    OrderOperateHistoryService orderOperateHistoryService;

    @Test
    void contextLoads() {
        OrderOperateHistoryEntity orderOperateHistoryEntity = new OrderOperateHistoryEntity();
        orderOperateHistoryEntity.setOrderId(1L);
        orderOperateHistoryEntity.setOperateMan("ceshi");
        orderOperateHistoryService.save(orderOperateHistoryEntity);
        System.out.println("orderOperateHistoryEntity保存成功");
    }

}
