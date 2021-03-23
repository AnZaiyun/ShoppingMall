package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.shoppingmall.product.dao.BrandDao;
import com.anzaiyun.shoppingmall.product.dao.CategoryDao;
import com.anzaiyun.shoppingmall.product.entity.BrandEntity;
import com.anzaiyun.shoppingmall.product.entity.CategoryEntity;
import com.anzaiyun.shoppingmall.product.service.BrandService;
import com.anzaiyun.shoppingmall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.CategoryBrandRelationDao;
import com.anzaiyun.shoppingmall.product.entity.CategoryBrandRelationEntity;
import com.anzaiyun.shoppingmall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long cateLogId = categoryBrandRelation.getCatelogId();

        BrandEntity brandEntity =  brandService.getById(brandId);
        CategoryEntity categoryEntity = categoryService.getById(cateLogId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        this.save(categoryBrandRelation);

    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        this.update(categoryBrandRelationEntity,new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));
    }

    @Override
    public void updateCategory(Long catId, String name) {
        //此处使用了与更新品牌方法（updateBrand）不同的实现逻辑，这里采用配置sql语句的方法进行更新。
        this.baseMapper.updateCategory(catId,name);
    }


}