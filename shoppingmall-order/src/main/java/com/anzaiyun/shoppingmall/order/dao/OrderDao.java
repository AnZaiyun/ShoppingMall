package com.anzaiyun.shoppingmall.order.dao;

import com.anzaiyun.shoppingmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 09:28:53
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
