package com.anzaiyun.shoppingmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 更新关联信息中的品牌id和品牌名
     * @param brandId
     * @param name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 更新关联信息中的分类id和分类名
     * @param catId
     * @param name
     */
    void updateCategory(Long catId,String name);
}

