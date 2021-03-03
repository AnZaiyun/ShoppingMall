package com.anzaiyun.shoppingmall.ware.service.impl;

import com.anzaiyun.shoppingmall.ware.entity.PurchaseDetailEntity;
import com.anzaiyun.shoppingmall.ware.entity.WareSkuEntity;
import com.anzaiyun.shoppingmall.ware.service.PurchaseDetailService;
import com.anzaiyun.shoppingmall.ware.service.WareSkuService;
import com.anzaiyun.shoppingmall.ware.vo.DoneItem;
import com.anzaiyun.shoppingmall.ware.vo.MergeVo;
import com.anzaiyun.shoppingmall.ware.vo.PurchaseDoneVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.Query;

import com.anzaiyun.shoppingmall.ware.dao.PurchaseDao;
import com.anzaiyun.shoppingmall.ware.entity.PurchaseEntity;
import com.anzaiyun.shoppingmall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<PurchaseEntity>();
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<PurchaseEntity>();

        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and(w->{
                w.like("id",key).or().like("assignee_id",key).or().
                        like("assignee_name",key);
            });
        }

        String status = (String) params.get("status");
        if(StringUtils.isNotEmpty(status)){
            queryWrapper.eq("status",status);
        }

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<PurchaseEntity>();
        wrapper.eq("status",0).or().eq("status",1);

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null){
            //无采购单，需要新建采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(0);
            purchaseEntity.setPriority(1);
            this.save(purchaseEntity);

            purchaseId = purchaseEntity.getId();
        }else {
            PurchaseEntity purchaseEntity = this.getById(purchaseId);
            if (purchaseEntity.getStatus() != 0){
                //对于非新建状态的采购单不做新建操作，这一点应该由前台进行校验
                return;
            }
        }

        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(1);

            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void recivePurchase(List<Long> purchaseIds) {
        //改变采购单的状态
        List<PurchaseEntity> collect = purchaseIds.stream().map(purchaseId -> {
            PurchaseEntity purchaseEntity = this.getById(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            return purchaseEntity;
        }).filter(item -> {
            return item.getStatus() == 1;
        }).map(item->{
            item.setStatus(2);
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(collect);

        //改变采购单关联采购需求的状态
        collect.forEach((item)->{
            purchaseDetailService.updatePurchaseDetailStatusByPurchaseId(item.getId());
        });
    }

    @Transactional
    @Override
    public void donePurchase(PurchaseDoneVo purchaseDoneVo) {
        //改变采购单状态
        @NotNull Long id = purchaseDoneVo.getId();
        PurchaseEntity purchaseEntity = this.getById(id);
        List<DoneItem> doneItems = purchaseDoneVo.getDoneItems();
        //如果关联的采购需求中有一项没完成，则整个采购单的状态置为异常
        doneItems.forEach(doneItem -> {
            if(doneItem.getStatus() == 4){
                purchaseEntity.setStatus(4);
            }
        });
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

        //改变采购单关联采购需求的状态
        List<PurchaseDetailEntity> collect = doneItems.stream().map(doneItem -> {
            PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(doneItem.getItemId());
            purchaseDetailEntity.setStatus(doneItem.getStatus());
            //TODO 如果失败，需要配置失败原因
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        //将成功采购的数据入库
        doneItems.forEach(doneItem -> {
            if(doneItem.getStatus() == 3){
                PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(doneItem.getItemId());
                //查询库存信息
                Long skuId = purchaseDetailEntity.getSkuId();
                Long wareId = purchaseDetailEntity.getWareId();
                WareSkuEntity wareSkuEntity = wareSkuService.getOne(new QueryWrapper<WareSkuEntity>().eq("sku_id",skuId).eq("ware_id",wareId));
                if(wareSkuEntity == null){
                    wareSkuEntity = new WareSkuEntity();
                    //仓库存储为空，需要新建一个存储
                    wareSkuEntity.setSkuId(skuId);
                    //TODO 需要调用product的微服务，获取sku的名字
                    wareSkuEntity.setSkuName("");
                    wareSkuEntity.setStock(purchaseDetailEntity.getSkuNum());
                    wareSkuEntity.setWareId(wareId);
                    wareSkuEntity.setStockLocked(0);
                    wareSkuService.save(wareSkuEntity);
                }else {
                    wareSkuEntity.setStock(wareSkuEntity.getStock()+purchaseDetailEntity.getSkuNum());
                    wareSkuService.updateById(wareSkuEntity);
                }
            }
        });


    }

}