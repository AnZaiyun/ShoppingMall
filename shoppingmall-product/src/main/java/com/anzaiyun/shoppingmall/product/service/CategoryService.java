package com.anzaiyun.shoppingmall.product.service;

import com.anzaiyun.shoppingmall.product.vo.Catalog2JsonVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    void addMenus(CategoryEntity category);

    Long[] findCatelogIds(Long catelogId);

    /**
     * 级联更新，需要一并更新相关的关联表
     * @param category
     */
    void updateCascade(CategoryEntity category);

    List<CategoryEntity> getCategoryByLevel(Long catLevel);

    /**
     * index.html页面获取所有的分类信息，返回数据格式为index/json/catalog.json中的格式
     * @return
     */
    Map<String, List<Catalog2JsonVo>> getCatalogJson();
}

