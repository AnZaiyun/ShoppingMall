package com.anzaiyun.shoppingmall.product.vo.ItemPage;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuAttrGroupVo {
    private Long groupId;
    private String groupName;
    private List<SpuBaseAttrVo> attrs;
}
