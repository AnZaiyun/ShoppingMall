package com.anzaiyun.shoppingmall.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.ware.dao.PurchaseDetailDao;
import com.anzaiyun.shoppingmall.ware.entity.PurchaseDetailEntity;
import com.anzaiyun.shoppingmall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<PurchaseDetailEntity>();

        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and(w->{
                w.like("id",key).or().like("purchase_id",key);
            });
        }

        String status = (String) params.get("status");
        if(StringUtils.isNotEmpty(status)){
            queryWrapper.eq("status",status);
        }

        String wareId = (String) params.get("wareId");
        if(StringUtils.isNotEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updatePurchaseDetailStatusByPurchaseId(Long id) {
        List<PurchaseDetailEntity> purchaseDetailList = this.list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        List<PurchaseDetailEntity> collect = purchaseDetailList.stream().map(item -> {
            item.setStatus(2);
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(collect);
    }

}