package com.anzaiyun.shoppingmall.product.service;

import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuItemVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SpuBaseAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.product.entity.AttrAttrgroupRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SpuBaseAttrVo> getDetailAttrByGroupId(Long spuId, Long attrGroupId);
}

