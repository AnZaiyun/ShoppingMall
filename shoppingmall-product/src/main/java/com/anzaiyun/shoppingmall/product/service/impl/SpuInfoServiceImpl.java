package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.common.to.SkuReductionAndLadderTo;
import com.anzaiyun.common.to.SpuBondsTo;
import com.anzaiyun.shoppingmall.product.entity.*;
import com.anzaiyun.shoppingmall.product.fegin.CouponFeginService;
import com.anzaiyun.shoppingmall.product.service.*;
import com.anzaiyun.shoppingmall.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    /**
     * 远程调用服务
     */
    @Autowired
    CouponFeginService couponFeginService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo spuInfoVo) {
        //1、保存Spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2、保存Spu描述图片 pms_spu_info_desc
        List<String> decript = spuInfoVo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3、保存spu图片信息 pms_spu_images
        List<String> images = spuInfoVo.getImages();
        spuImagesService.saveSpuImages(spuInfoEntity.getId(),images);

        //4、保存spu规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfoVo.getBaseAttrs();
        productAttrValueService.saveBaseAttrs(spuInfoEntity.getId(),baseAttrs);

        //5、保存spu对应的积分信息 shoppingmall_sms.sms_spu_bounds
        Bounds bounds = spuInfoVo.getBounds();
        SpuBondsTo spuBondsTo = new SpuBondsTo();
        BeanUtils.copyProperties(bounds,spuBondsTo);
        couponFeginService.saveSpuBounds(spuBondsTo);

        //6、保存spu对应的sku信息
        List<Skus> skus = spuInfoVo.getSkus();
        skus.forEach(sku->{
            //获取默认图片
            String defaultImg = "";
            for(Images img:sku.getImages()){
                if(img.getDefaultImg() == 1){
                    defaultImg = img.getImgUrl();
                }
            }
            //6.1、sku基本信息 pms_sku_info
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku,skuInfoEntity);
            skuInfoEntity.setBrandId(spuInfoVo.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoVo.getCatalogId());
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setSkuDefaultImg(defaultImg);
            skuInfoService.save(skuInfoEntity);

            Long skuId = skuInfoEntity.getSkuId();

            //6.2、sku图片信息 pms_sku_images
            List<Images> skuImages = sku.getImages();
            List<SkuImagesEntity> skuImgCollect = skuImages.stream().map(skuImg -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setImgUrl(skuImg.getImgUrl());
                skuImagesEntity.setDefaultImg(skuImg.getDefaultImg());
                skuImagesEntity.setSkuId(skuId);

                return skuImagesEntity;
            }).filter(entity->{
                return StringUtils.isNotEmpty(entity.getImgUrl());
            }).collect(Collectors.toList());
            skuImagesService.saveBatch(skuImgCollect);

            //6.3、sku的销售信息 pms_sku_sale_attr_value
            List<Attr> attrs = sku.getAttr();
            List<SkuSaleAttrValueEntity> skuAttrCollect = attrs.stream().map(attr -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);

                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuAttrCollect);

            //6.4、sku的优惠，满减信息 shoppingmall_sms.sms_sku_ladder、sms_sku_full_reduction
            SkuReductionAndLadderTo skuReductionAndLadderTo = new SkuReductionAndLadderTo();
            BeanUtils.copyProperties(sku,skuReductionAndLadderTo);
            skuReductionAndLadderTo.setSkuId(skuId);
            if (skuReductionAndLadderTo.getFullCount() > 0 || skuReductionAndLadderTo.getFullPrice().compareTo(new BigDecimal('0')) == 1){
                couponFeginService.saveSkuReductionAndLadder(skuReductionAndLadderTo);
            }


        });

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}