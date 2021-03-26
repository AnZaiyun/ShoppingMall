package com.anzaiyun.shoppingmall.search.service.impl;

import com.anzaiyun.shoppingmall.search.constant.ProductConstant;
import com.anzaiyun.shoppingmall.search.service.SearchProductService;
import com.anzaiyun.shoppingmall.search.vo.SearchParam;
import com.anzaiyun.shoppingmall.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service("searchProduct")
public class SearchProductServiceImpl implements SearchProductService {

    @Autowired
    private RestHighLevelClient client;

    public SearchSourceBuilder makerDslStatement(SearchParam searchParam){
        //配置dsl
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //增加skutitle检索条件
        MatchQueryBuilder skuTitle = new MatchQueryBuilder("skuTitle", searchParam.getKeyword());
        boolQueryBuilder.must(skuTitle);

        //构建filter检索
        TermQueryBuilder catalogId = new TermQueryBuilder("catalogId", searchParam.getCatalog3Id());
        boolQueryBuilder.filter(catalogId);
        TermsQueryBuilder brandId = new TermsQueryBuilder("brandId", searchParam.getBrandId());
        boolQueryBuilder.filter(brandId);
        RangeQueryBuilder skuPrice = new RangeQueryBuilder("skuPrice");
        String[] s = searchParam.getSkuPrice().split("_");
        skuPrice.gte(s[0]);
        skuPrice.lte(s[1]);
        boolQueryBuilder.filter(skuPrice);
        TermQueryBuilder hasStock = new TermQueryBuilder("hasStock", searchParam.getHasStock());
        boolQueryBuilder.filter(hasStock);
        //构建attrs检索条件
        TermQueryBuilder attrId = new TermQueryBuilder("attrs.attrId", null);
        TermsQueryBuilder attrValue = new TermsQueryBuilder("attrs.attrValue", new ArrayList());
        BoolQueryBuilder attrs = new BoolQueryBuilder();
        attrs.must(attrId).must(attrValue);
        NestedQueryBuilder attrsNested = new NestedQueryBuilder("attrs", attrs, null);
        boolQueryBuilder.filter(attrsNested);

        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.sort("skuPrice", SortOrder.ASC);

        searchSourceBuilder.from(0).size(10);

        HighlightBuilder highlightBuilder = new HighlightBuilder().field("skuTitle").preTags("<b style='color:red'>").postTags("</b>");
        searchSourceBuilder.highlighter(highlightBuilder);

        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(10);
        TermsAggregationBuilder catalog_name_agg = AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(10);
        catalog_agg.subAggregation(catalog_name_agg);

        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(10);
        TermsAggregationBuilder brand_name_agg = AggregationBuilders.terms("brand_name_agg").field("brandName").size(10);
        TermsAggregationBuilder brand_img_agg = AggregationBuilders.terms("brand_img_agg").field("brandImg").size(10);
        brand_agg.subAggregation(brand_name_agg).subAggregation(brand_img_agg);

        NestedAggregationBuilder attrs_agg = AggregationBuilders.nested("attrs_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrId").size(10);
        TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrName").size(10);
        TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrValue").size(10);
        attr_id_agg.subAggregation(attr_name_agg).subAggregation(attr_value_agg);
        attrs_agg.subAggregation(attr_id_agg);

        searchSourceBuilder.aggregation(catalog_agg).aggregation(brand_agg).aggregation(attrs_agg);

        return searchSourceBuilder;
    }

    @Override
    public SearchResult search(SearchParam searchParam)  {

        //构建查询的dsl语句
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ProductConstant.PRODUCT_INDEX);

        SearchSourceBuilder searchSourceBuilder = makerDslStatement(searchParam);

        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
