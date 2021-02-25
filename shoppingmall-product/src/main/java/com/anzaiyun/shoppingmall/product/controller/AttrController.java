package com.anzaiyun.shoppingmall.product.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.anzaiyun.shoppingmall.product.entity.AttrAttrgroupRelationEntity;
import com.anzaiyun.shoppingmall.product.service.AttrAttrgroupRelationService;
import com.anzaiyun.shoppingmall.product.vo.AttrRespVo;
import com.anzaiyun.shoppingmall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anzaiyun.shoppingmall.product.service.AttrService;
import com.anzaiyun.common.utils.PageUtils;
import com.anzaiyun.common.utils.R;



/**
 * 商品属性
 *
 * @author anzaiyun
 * @email anzaiyun@gmail.com
 * @date 2020-10-27 23:22:05
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取基本的规格参数信息
     * @param params
     * @param catId
     * @return
     */
    @RequestMapping("/{attrType}/list/{catId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catId") Long catId,
                          @PathVariable("attrType") String attrType){

        PageUtils page = attrService.queryBaseAttrpage(params,catId,attrType);
        return R.ok().put("page",page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);

		AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

		//同时删除关联表中的数据
        if (attrIds.length>0) {
            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", attrIds[0]);
            for (Long attrId : attrIds) {
                queryWrapper.or().eq("attr_id", attrId);
            }
            attrgroupRelationService.remove(queryWrapper);
        }
        return R.ok();
    }

}
