package com.anzaiyun.shoppingmall.product.service;

import com.anzaiyun.shoppingmall.product.entity.AttrEntity;
import com.anzaiyun.shoppingmall.product.vo.AttrGroupWithAttrsVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuItemVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SpuAttrGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrEntity> queryAttrByGroupId(Map<String, Object> params, Long groupId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catId);

    List<SpuAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId);

    List<SpuAttrGroupVo> getAttrGroupWithAttrsBySpuIdSimple(Long spuId);
}

