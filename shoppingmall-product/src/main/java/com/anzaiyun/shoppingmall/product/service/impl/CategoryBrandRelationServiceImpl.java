package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.shoppingmall.product.entity.BrandEntity;
import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;
import com.anzaiyun.shoppingmall.product.service.BrandService;
import com.anzaiyun.shoppingmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.CategoryBrandRelationDao;
import com.anzaiyun.shoppingmall.product.entity.CategoryBrandRelationEntity;
import com.anzaiyun.shoppingmall.product.service.CategoryBrandRelationService;
import org.springframework.util.StringUtils;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String brandId = (String) params.get("brandId");
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(brandId)){
            queryWrapper.eq("brand_id",brandId);
        }
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void saveWithName(CategoryBrandRelationEntity categoryBrandRelation) {
        //需要进行校验避免重复插入
        Map<String,Object> params = new HashMap<>();
        params.put("catelog_id",categoryBrandRelation.getCatelogId());
        params.put("brand_id",categoryBrandRelation.getBrandId());
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities =  baseMapper.selectByMap(params);

        if(categoryBrandRelationEntities.size() == 0) {
            CategoryEntity category = categoryService.getById(categoryBrandRelation.getCatelogId());
            BrandEntity brand = brandService.getById(categoryBrandRelation.getBrandId());

            categoryBrandRelation.setCatelogName(category.getName());
            categoryBrandRelation.setBrandName(brand.getName());

            baseMapper.insert(categoryBrandRelation);
        }


    }

}