package com.anzaiyun.shoppingmall.product.service;

import com.anzaiyun.shoppingmall.product.vo.AttrRespVo;
import com.anzaiyun.shoppingmall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrpage(Map<String, Object> params, Long catId);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

}

