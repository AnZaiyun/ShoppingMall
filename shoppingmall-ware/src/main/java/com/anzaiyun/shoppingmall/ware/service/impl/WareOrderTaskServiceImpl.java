package com.anzaiyun.shoppingmall.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.ware.dao.WareOrderTaskDao;
import com.anzaiyun.shoppingmall.ware.entity.WareOrderTaskEntity;
import com.anzaiyun.shoppingmall.ware.service.WareOrderTaskService;
import org.springframework.util.StringUtils;


@Service("wareOrderTaskService")
public class WareOrderTaskServiceImpl extends ServiceImpl<WareOrderTaskDao, WareOrderTaskEntity> implements WareOrderTaskService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                new QueryWrapper<WareOrderTaskEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<WareOrderTaskEntity> queryWrapper = new QueryWrapper<WareOrderTaskEntity>();
        if(!StringUtils.isEmpty(key)){
            queryWrapper.like("id",key).or().like("order_id",key).or().like("consignee",key);
        }
        IPage<WareOrderTaskEntity> page = this.page(
                new Query<WareOrderTaskEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}