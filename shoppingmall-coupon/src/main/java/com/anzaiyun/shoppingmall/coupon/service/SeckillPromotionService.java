package com.anzaiyun.shoppingmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 21:08:01
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

