package com.anzaiyun.shoppingmall.ware.fegin;

import com.anzaiyun.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("shoppingmall-product")
public interface ProductFeginService {

    @PostMapping("/product/skuinfo/getSkuinfo")
    public R getSkuinfo(@RequestBody Long skuId);

}
