package com.anzaiyun.shoppingmall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.anzaiyun.shoppingmall.ware.vo.MergeVo;
import com.anzaiyun.shoppingmall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.anzaiyun.shoppingmall.ware.entity.PurchaseEntity;
import com.anzaiyun.shoppingmall.ware.service.PurchaseService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.R;



/**
 * 采购信息
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-28 21:01:55
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     * 带条件查询 http://localhost:88/api/ware/purchase/list?t=1614739687653&page=1&limit=10&key=&status=
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
        purchase.setUpdateTime(new Date());
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 合并采购单，查询未领取的采购需求
     * http://localhost:88/api/ware/purchase/unreceive/list?t=1614774299330
     */
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceivePurchase(params);

        return R.ok().put("page", page);
    }

    /**
     * 合并采购单，合并采购需求
     * http://localhost:88/api/ware/purchase/merge
     */
    @PostMapping("/merge")
    //@RequiresPermissions("ware:purchase:list")
    public R merge(@RequestBody MergeVo mergeVo){

        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }

    /**
     * 采购人员接取采购单
     * http://localhost:88/api/ware/purchase/recived
     */
    @PostMapping("/recived")
    //@RequiresPermissions("ware:purchase:list")
    public R recived(@RequestBody List<Long> purchaseIds){

        purchaseService.recivePurchase(purchaseIds);

        return R.ok();
    }

    /**
     * 采购人员完成采购单
     * http://localhost:88/api/ware/purchase/done
     */
    @PostMapping("/done")
    //@RequiresPermissions("ware:purchase:list")
    public R done(@RequestBody PurchaseDoneVo purchaseDoneVo){

        purchaseService.donePurchase(purchaseDoneVo);

        return R.ok();
    }

}
