package com.anzaiyun.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBondsTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
