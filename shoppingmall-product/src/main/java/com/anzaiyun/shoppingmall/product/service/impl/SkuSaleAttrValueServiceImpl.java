package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.SkuSaleAttrValueDao;
import com.anzaiyun.shoppingmall.product.entity.SkuSaleAttrValueEntity;
import com.anzaiyun.shoppingmall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuSaleAttrVo> getSKuSaleAttrBySpuId(Long spuId) {

        List<SkuSaleAttrVo> skuSaleAttrVos = this.baseMapper.getSKuSaleAttrBySpuId(spuId);

        return skuSaleAttrVos;
    }

    @Override
    public List<String> getSaleAttrAsStringList(Long skuId) {

        List<String> saleAttrs = this.baseMapper.getSaleAttrAsStringList(skuId);
        return saleAttrs;
    }

}