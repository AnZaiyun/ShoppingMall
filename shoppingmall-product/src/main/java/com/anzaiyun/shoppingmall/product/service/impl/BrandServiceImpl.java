package com.anzaiyun.shoppingmall.product.service.impl;

import com.anzaiyun.shoppingmall.product.service.CategoryBrandRelationService;
import com.mysql.cj.util.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.BrandDao;
import com.anzaiyun.shoppingmall.product.entity.BrandEntity;
import com.anzaiyun.shoppingmall.product.service.BrandService;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //支持前台查询功能
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<BrandEntity>();

        if (!StringUtils.isNullOrEmpty(key)) {
            wrapper.eq("brand_id",key).or() .like("name",key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


    @Override
    public void updateDetail(BrandEntity brand) {
        this.updateById(brand);
        if(!StringUtils.isNullOrEmpty(brand.getName())){
            //同步更新其他关联表的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
        }


    }

}