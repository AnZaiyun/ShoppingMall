package com.anzaiyun.shoppingmall.product.dao;

import com.anzaiyun.shoppingmall.product.entity.AttrAttrgroupRelationEntity;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuItemVo;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SpuBaseAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    List<SpuBaseAttrVo> getDetailAttrByGroupId(@Param("spuId") Long spuId, @Param("attrGroupId") Long attrGroupId);
}
