package com.anzaiyun.shoppingmall.product.vo;

import com.anzaiyun.shoppingmall.product.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrRespVo extends AttrEntity {

    private Long attrGroupId;

    //所属分类名字
    private String catelogName;

    //所属分组名字
    private String groupName;

    private Long[] catelogPath;

}
