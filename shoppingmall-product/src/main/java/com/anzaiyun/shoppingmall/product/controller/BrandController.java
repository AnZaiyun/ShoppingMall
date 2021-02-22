package com.anzaiyun.shoppingmall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.anzaiyun.common.valid.AddGroup;
import com.anzaiyun.common.valid.UpdateGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anzaiyun.shoppingmall.product.entity.BrandEntity;
import com.anzaiyun.shoppingmall.product.service.BrandService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:brand:save")
    public R save(@Validated(AddGroup.class) @RequestBody BrandEntity brand){
		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    //@RequiresPermissions("product:brand:update")
//    public R update(@Valid @RequestBody BrandEntity brand, BindingResult result){
//        if (result.hasErrors()) {
//            Map<String,String> errorMsg = new HashMap<>();
//            result.getFieldErrors().forEach((item)->{
//                String message = item.getDefaultMessage();
//                String field = item.getField();
//                errorMsg.put(field,message);
//            });
//            return R.error(400,"提交数据不合法").put("data",errorMsg);
//        }else {
//            brandService.updateById(brand);
//            return R.ok();
//        }
//    }

    /**
     * 使用通知的方式来处理异常
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
        brandService.updateDetail(brand);
        return R.ok();
    }

    /**
     * 这里只用来修改状态字段
     * @param brand
     * @return
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("product:brand:update")
    public R updateStatus(@RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
