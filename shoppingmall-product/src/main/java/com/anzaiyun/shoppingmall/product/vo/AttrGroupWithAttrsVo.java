package com.anzaiyun.shoppingmall.product.vo;

import com.anzaiyun.shoppingmall.product.entity.AttrEntity;
import com.anzaiyun.shoppingmall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * 包含当前分组下的规格参数信息
 */
@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {

    /**
     * 与分组id对应的规格参数信息
     */
    private List<AttrEntity> attrs;
}
