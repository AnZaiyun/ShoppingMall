package com.anzaiyun.shoppingmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.anzaiyun.common.to.SkuReductionAndLadderTo;
import com.anzaiyun.common.to.SpuBondsTo;
import com.anzaiyun.common.to.es.SkuEsModelTo;
import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.product.entity.*;
import com.anzaiyun.shoppingmall.product.fegin.CouponFeginService;
import com.anzaiyun.shoppingmall.product.fegin.SearchFeginService;
import com.anzaiyun.shoppingmall.product.fegin.WareFeginService;
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

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AttrService attrService;

    @Autowired
    WareFeginService wareFeginService;

    @Autowired
    SearchFeginService searchFeginService;

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
    @Transactional(rollbackFor = {})
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
            try{
                skuSaleAttrValueService.saveBatch(skuAttrCollect);
            }catch (Exception e){
                log.error("sku销售信息保存失败:{}",e);
            }

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

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and((w)-> w.eq("id",key).or().like("spu_name",key).or().like("spu_description",key));
        }

        String status = (String) params.get("status");
        if(StringUtils.isNotEmpty(status)){
            queryWrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(StringUtils.isNotEmpty(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(StringUtils.isNotEmpty(catelogId)){
            queryWrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void upSpu(Long spuId) {

        //组装需要的数据

        //查询sku所有可以被检索的规格属性信息
        List<ProductAttrValueEntity> productAttrValueList = productAttrValueService.getBySpuId(spuId);
        //获取所有spu包含的给规格属性id
        List<Long> attrIdList = productAttrValueList.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());
        //根据传入的规格属性id列表，获取所有可被检索的规格属性id
        List<Long> searchAttrIdList = attrService.getSearchAttrIdById(attrIdList);
        //spu信息中的属性id如果属于可被检索的id，则返回SkuEsModelTo.attr用于es保存
        List<SkuEsModelTo.Attr> collect1 = productAttrValueList.stream().filter(item -> searchAttrIdList.contains(item.getAttrId())).map(attr -> {
            SkuEsModelTo.Attr attr1 = new SkuEsModelTo.Attr();
            BeanUtils.copyProperties(attr, attr1);
            return attr1;
        }).collect(Collectors.toList());

        //查询sku所有可以被检索的规格属性信息 这里其实也可以多表关联查询，直接写sql返回结果，这样只用调用一个方法即可
        // select a.* from pms_attr a,pms_product_attr_value b
        // where a.attr_id = b.attr_id and b.spu_id = #{spuId} and a.search_type = 1
//        List<Attr> searchAttr = productAttrValueService.getSearchAttrBySpuId(spuId);

        //查出当前spuid对应的所有sku信息
        List<SkuInfoEntity> skuList = skuInfoService.getSkuInfoBySpuId(spuId);

        List<Long> skuIds = skuList.stream().map(sku -> sku.getSkuId()).collect(Collectors.toList());
        //发送远程调用查询是否有库存，hasStock
        Map<Long, Boolean> skuStockMap = null;
        try {
            R listR = wareFeginService.hasSkuStock(skuIds);
            Object data = listR.get("data");
            if(data != null) {
                String s = JSON.toJSONString(data);
                TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
                };
                List<SkuHasStockVo> skuHasStockVos = JSON.parseObject(s, typeReference);
                skuStockMap = skuHasStockVos.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
            }
        }catch (Exception e){
            log.error("库存服务查询失败：{}",e);
        }


        Map<Long, Boolean> finalSkuStockMap = skuStockMap;
        List<SkuEsModelTo> skuEsModelToList = skuList.stream().map(sku -> {
            SkuEsModelTo skuEsModel = new SkuEsModelTo();
            BeanUtils.copyProperties(sku,skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());

            //设置库存信息
            if (finalSkuStockMap == null || finalSkuStockMap.size() ==0){
                skuEsModel.setHasStock(false);
            }else {
                skuEsModel.setHasStock(finalSkuStockMap.get(sku.getSkuId()));
            }

            //热度评分，默认置零
            skuEsModel.setHotScore(0L);
            //查询品牌名字和图片
            BrandEntity brand = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(category.getName());

            //设置规格属性信息
            skuEsModel.setAttrs(collect1);

            return skuEsModel;
        }).collect(Collectors.toList());


        System.out.println(skuEsModelToList); //打印检查

        if(skuEsModelToList != null && skuEsModelToList.size() > 0) {
            //发送远程请求，数据保存到es
            try {
                searchFeginService.upProduct(skuEsModelToList);
            } catch (Exception e) {
                log.error("检索服务调用失败：{}", e);
            }

            //更新商品状态为上架
            SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);
            spuInfoEntity.setPublishStatus(1);
            this.baseMapper.updateById(spuInfoEntity);
        }
    }

}