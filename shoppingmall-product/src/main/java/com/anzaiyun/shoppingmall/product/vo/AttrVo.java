package com.anzaiyun.shoppingmall.product.vo;

import com.anzaiyun.shoppingmall.product.entity.AttrEntity;
import lombok.Data;

/**
 * po即数据库中某张表的某条记录的实例对象
 * vo则是对po的一个在封装，其中的某个字段可能在数据库中不存在
 */

@Data
public class AttrVo extends AttrEntity {

    private Long attrGroupId;
}
