package com.anzaiyun.shoppingmall.search.vo;

import com.anzaiyun.common.to.es.SkuEsModelTo;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    /**
     * 查询到的所有商品信息
     */
    private List<SkuEsModelTo> products;

    /**
     * 当前页码
     */
    private Long pageNum;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页码
     */
    private Long totalPages;

    /**
     * 所有涉及到的品牌
     */
    private List<BrandVo> brands;
    /**
     * 所有涉及到的分类
     */
    private List<CatalogVo> catalogs;
    /**
     * 所有涉及到的属性
     */
    private List<AttrVo> attrs;

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
