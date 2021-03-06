package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.shoppingmall.product.entity.SkuImagesEntity;
import com.anzaiyun.shoppingmall.product.entity.SpuInfoDescEntity;
import com.anzaiyun.shoppingmall.product.service.*;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuItemVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuSaleAttrVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SpuAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.SkuInfoDao;
import com.anzaiyun.shoppingmall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor threadPool;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<SkuInfoEntity>();

        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and((w)->{
                w.eq("sku_id",key).or().like("sku_name",key).or().like("sku_desc",key);
            });
        }

        String minPrice = (String) params.get("min");
        if(StringUtils.isNotEmpty(minPrice) && !"0".equals(minPrice)){
            queryWrapper.gt("price",minPrice);
        }

        String maxPrice = (String) params.get("max");
        if(StringUtils.isNotEmpty(maxPrice) && !"0".equals(maxPrice)){
            queryWrapper.lt("price",maxPrice);
        }

        String brandId = (String) params.get("brandId");
        if(StringUtils.isNotEmpty(brandId) && !"0".equals(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(StringUtils.isNotEmpty(catelogId) && !"0".equals(catelogId)){
            queryWrapper.eq("catalog_id",catelogId);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId) {

        List<SkuInfoEntity> skuInfoEntityList = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
        return skuInfoEntityList;
    }

    /**
     * 获取商品详情信息
     * @param skuId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public SkuItemVo getSkuItem(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            //查询基本信息
            SkuInfoEntity skuInfo = getById(skuId);
            skuItemVo.setSkuInfo(skuInfo);

            return skuInfo;
        }, threadPool);

        CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            //sku图片信息
            List<SkuImagesEntity> skuImages = skuImagesService.getIamgeBySkuId(skuId);
            skuItemVo.setSkuImages(skuImages);
        }, threadPool);


        CompletableFuture<Void> spuInfoDescFuture = skuInfoFuture.thenAcceptAsync((sku) -> {
            //spu的介绍
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(sku.getSpuId());
            skuItemVo.setSpuDesc(spuInfo);
        }, threadPool);

        CompletableFuture<Void> skuSaleAttrFuture = skuInfoFuture.thenAcceptAsync((sku) -> {
            //spu销售属性组合
            List<SkuSaleAttrVo> skuSaleAttrVos = skuSaleAttrValueService.getSKuSaleAttrBySpuId(sku.getSpuId());
            skuItemVo.setSaleAttrs(skuSaleAttrVos);
        }, threadPool);

//        skuInfoFuture.thenAcceptAsync((sku)-> {
//            //spu的规格参数信息
//            List<SpuAttrGroupVo> spuAttrGroup = attrGroupService.getAttrGroupWithAttrsBySpuId(sku.getSpuId());
//            skuItemVo.setGroupAttrs(spuAttrGroup);
//        }, threadPool);

        CompletableFuture<Void> spuAttrGroupFuture = skuInfoFuture.thenAcceptAsync((sku) -> {
            //以上的方法比较复杂，需要查询两遍sql，其实也可以使用mybatis的结果集封装功能，自动将数据转为想要的groupvo格式
            List<SpuAttrGroupVo> spuAttrGroup2 = attrGroupService.getAttrGroupWithAttrsBySpuIdSimple(sku.getSpuId());
            skuItemVo.setGroupAttrs(spuAttrGroup2);
        }, threadPool);

        //全部执行成功，返回结果
        CompletableFuture.allOf(skuImagesFuture, spuInfoDescFuture, skuSaleAttrFuture, spuAttrGroupFuture).get();

        return skuItemVo;

    }

}