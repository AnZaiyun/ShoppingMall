package com.anzaiyun.shoppingmall.product.service;

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

}

