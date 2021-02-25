package com.anzaiyun.shoppingmall.coupon.service.impl;

import com.anzaiyun.common.to.MemberPrice;
import com.anzaiyun.common.to.SkuReductionAndLadderTo;
import com.anzaiyun.shoppingmall.coupon.entity.MemberPriceEntity;
import com.anzaiyun.shoppingmall.coupon.entity.SkuLadderEntity;
import com.anzaiyun.shoppingmall.coupon.service.MemberPriceService;
import com.anzaiyun.shoppingmall.coupon.service.SkuLadderService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.coupon.dao.SkuFullReductionDao;
import com.anzaiyun.shoppingmall.coupon.entity.SkuFullReductionEntity;
import com.anzaiyun.shoppingmall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReductionAndLadder(SkuReductionAndLadderTo skuReductionAndLadderTo) {
        //1、保存满减打折信息  sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionAndLadderTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionAndLadderTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionAndLadderTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionAndLadderTo.getCountStatus()); //  是否叠加其他优惠
        if(skuLadderEntity.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }

        //2、保存满减优惠信息  sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionAndLadderTo,skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
            this.save(skuFullReductionEntity);
        }

        //3、保存会员价格信息  sms_member_price
        List<MemberPrice> memberPrices = skuReductionAndLadderTo.getMemberPrices();
        List<MemberPriceEntity> collect = memberPrices.stream().map(memberPrice -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionAndLadderTo.getSkuId());
            memberPriceEntity.setMemberLevelId(memberPrice.getId());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setAddOther(1);

            return memberPriceEntity;

        }).filter(memberPrice ->{
            return memberPrice.getMemberPrice().compareTo(new BigDecimal("0")) == 1;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(collect);

    }

}