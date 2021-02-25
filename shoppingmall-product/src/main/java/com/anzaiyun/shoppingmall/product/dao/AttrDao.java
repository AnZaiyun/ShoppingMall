package com.anzaiyun.shoppingmall.product.dao;

import com.anzaiyun.shoppingmall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品属性
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    void updateAttrGroupIdByAttrId(@Param("attrId") Long attrId, @Param("attrGroupId") Long attrGroupId);
}
