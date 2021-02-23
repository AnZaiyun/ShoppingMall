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

//    这个字段在数据库中不存在，教程视频目前也未用到这个字段，这里先顺便给一个值，保证前台不报错
    private Integer valueType = 1;

}
