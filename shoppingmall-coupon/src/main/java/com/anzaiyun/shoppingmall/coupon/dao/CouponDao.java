package com.anzaiyun.shoppingmall.coupon.dao;

import com.anzaiyun.shoppingmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 21:08:01
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
