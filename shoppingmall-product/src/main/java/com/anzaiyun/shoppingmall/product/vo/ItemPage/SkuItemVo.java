package com.anzaiyun.shoppingmall.product.vo.ItemPage;

import com.anzaiyun.shoppingmall.product.entity.SkuImagesEntity;
import com.anzaiyun.shoppingmall.product.entity.SkuInfoEntity;
import com.anzaiyun.shoppingmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    SkuInfoEntity skuInfo;

    List<SkuImagesEntity> skuImages;

    SpuInfoDescEntity spuDesc;

    List<SkuSaleAttrVo> saleAttrs;

    List<SpuAttrGroupVo> groupAttrs;

}
