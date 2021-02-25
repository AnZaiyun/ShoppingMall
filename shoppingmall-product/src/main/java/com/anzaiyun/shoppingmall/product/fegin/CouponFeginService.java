package com.anzaiyun.shoppingmall.product.fegin;

import com.anzaiyun.common.to.SkuReductionAndLadderTo;
import com.anzaiyun.common.to.SpuBondsTo;
import com.anzaiyun.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 声明式远程调用
 */
@FeignClient("shoppingmall-coupon")
public interface CouponFeginService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBondsTo spuBondsTo);

    @PostMapping("/coupon/skufullreduction/saveReductionAndLadder")
    R saveSkuReductionAndLadder(@RequestBody SkuReductionAndLadderTo skuReductionAndLadderTo);
}
