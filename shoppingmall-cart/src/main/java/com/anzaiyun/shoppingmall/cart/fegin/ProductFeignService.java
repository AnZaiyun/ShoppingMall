package com.anzaiyun.shoppingmall.cart.fegin;

import com.anzaiyun.shoppingmall.cart.vo.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("shoppingmall-product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/getInfo")
    public SkuInfoVo getInfo(@RequestParam("skuId") Long skuId);

    /**
     * 获取销售属性，以字符串列表的方式返回
     * 每一项的格式为attr_name+":"+attr_value
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skusaleattrvalue/getSaleAttrAsStringList")
    public List<String> getSaleAttrAsStringList(@RequestParam("skuId") Long skuId);
}
