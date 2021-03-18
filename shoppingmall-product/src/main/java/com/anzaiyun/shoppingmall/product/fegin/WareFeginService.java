package com.anzaiyun.shoppingmall.product.fegin;

import com.anzaiyun.common.utils.R;
import com.anzaiyun.shoppingmall.product.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("shoppingmall-ware")
public interface WareFeginService {

    @PostMapping("/ware/waresku/hasStock")
    R hasSkuStock(@RequestBody List<Long> skuIds);
}
