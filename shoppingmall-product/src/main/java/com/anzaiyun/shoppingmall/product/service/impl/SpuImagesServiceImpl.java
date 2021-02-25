package com.anzaiyun.shoppingmall.product.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.product.dao.SpuImagesDao;
import com.anzaiyun.shoppingmall.product.entity.SpuImagesEntity;
import com.anzaiyun.shoppingmall.product.service.SpuImagesService;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuImages(Long id, List<String> images) {
        if(images == null || images.size() == 0){
            return;
        }else {
            List<SpuImagesEntity> spuImageCollect = images.stream().map(
                    image -> {
                        SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                        spuImagesEntity.setImgUrl(image);
                        spuImagesEntity.setSpuId(id);
                        return spuImagesEntity;
                    }
            ).collect(Collectors.toList());

            this.saveBatch(spuImageCollect);
        }
    }

}