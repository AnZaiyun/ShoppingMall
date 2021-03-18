package com.anzaiyun.shoppingmall.product.fegin;

import com.anzaiyun.common.to.es.SkuEsModelTo;
import com.anzaiyun.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("shoppingmall-search")
public interface SearchFeginService {

    @PostMapping("/search/product/upproduct")
    public R upProduct(@RequestBody List<SkuEsModelTo> skuEsModelList);

}
