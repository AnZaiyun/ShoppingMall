package com.anzaiyun.shoppingmall.product.dao;

import com.anzaiyun.shoppingmall.product.entity.ProductAttrValueEntity;
import com.anzaiyun.shoppingmall.product.vo.Attr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    List<Attr> getSearchAttrBySpuId(@Param("spuId") Long spuId);
}
