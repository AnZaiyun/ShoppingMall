package com.anzaiyun.shoppingmall.product.service;

import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuSaleAttrVo> getSKuSaleAttrBySpuId(Long spuId);
}

