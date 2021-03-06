package com.anzaiyun.shoppingmall.product.service;

import com.anzaiyun.common.to.es.SkuEsModelTo;
import com.anzaiyun.shoppingmall.product.vo.Attr;
import com.anzaiyun.shoppingmall.product.vo.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBaseAttrs(Long id, List<BaseAttrs> baseAttrs);

    List<ProductAttrValueEntity> getBySpuId(Long spuId);

    List<Attr> getSearchAttrBySpuId(Long spuId);
}

