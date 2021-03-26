package com.anzaiyun.shoppingmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面可能传递过来的查询条件
 */
@Data
public class SearchParam {

    /**
     * 全文匹配关键字
     */
    private String keyword;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件，格式如下
     * sort=排序字段_升序/降序
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     */
    private String sort;

    /**
     * 是否有货
     * hasStock=0/1
     */
    private Long hasStock;

    /**
     * 价格区间查询
     * skuPrice=上限_下限，上限或下限之一可以为空
     * skuPrice=1_500/_500/500_
     */
    private String skuPrice;

    /**
     * 品牌id
     */
    private List<Long> brandId;

    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Long pageNum;
}
