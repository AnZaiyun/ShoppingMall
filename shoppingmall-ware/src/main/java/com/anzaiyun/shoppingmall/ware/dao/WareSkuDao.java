package com.anzaiyun.shoppingmall.ware.dao;

import com.anzaiyun.shoppingmall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 21:01:55
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
