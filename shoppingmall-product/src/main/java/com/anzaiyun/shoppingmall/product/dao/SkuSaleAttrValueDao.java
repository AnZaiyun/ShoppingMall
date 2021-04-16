package com.anzaiyun.shoppingmall.product.dao;

import com.anzaiyun.shoppingmall.product.entity.SkuSaleAttrValueEntity;
import com.anzaiyun.shoppingmall.product.vo.ItemPage.SkuSaleAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuSaleAttrVo> getSKuSaleAttrBySpuId(@Param("spuId") Long spuId);

    List<String> getSaleAttrAsStringList(@Param("skuId") Long skuId);
}
