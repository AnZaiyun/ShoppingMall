package com.anzaiyun.shoppingmall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.coupon.dao.CouponDao;
import com.anzaiyun.shoppingmall.coupon.entity.CouponEntity;
import com.anzaiyun.shoppingmall.coupon.service.CouponService;


@Service("couponService")
public class CouponServiceImpl extends ServiceImpl<CouponDao, CouponEntity> implements CouponService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CouponEntity> page = this.page(
                new Query<CouponEntity>().getPage(params),
                new QueryWrapper<CouponEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<CouponEntity> queryWrapper = new QueryWrapper<CouponEntity>();
        queryWrapper.eq("id",key).or().like("coupon_name",key);
        IPage<CouponEntity> page = this.page(
                new Query<CouponEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}