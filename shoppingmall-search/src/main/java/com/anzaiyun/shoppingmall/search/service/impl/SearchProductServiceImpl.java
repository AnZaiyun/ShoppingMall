package com.anzaiyun.shoppingmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.anzaiyun.common.to.es.SkuEsModelTo;
import com.anzaiyun.shoppingmall.search.constant.ProductConstant;
import com.anzaiyun.shoppingmall.search.service.SearchProductService;
import com.anzaiyun.shoppingmall.search.vo.SearchParam;
import com.anzaiyun.shoppingmall.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author AnZaiYun
 */
@Service("searchProduct")
public class SearchProductServiceImpl implements SearchProductService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * java中构建出dsl语句的关键就是要先在elasticsearch中把查询逻辑构建出，然后按照逻辑嵌套即可
     * dsl语句查询，/resources/sql/elasticsearch
     * @param searchParam
     * @return
     */
    public SearchSourceBuilder makerDslStatement(SearchParam searchParam){
        //配置dsl
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1、构建bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1、增加skutitle检索条件
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            MatchQueryBuilder skuTitle = new MatchQueryBuilder("skuTitle", searchParam.getKeyword());
            boolQueryBuilder.must(skuTitle);
        }


        //1.2、构建filter检索
        //1.2.1、构建三级分类ID过滤条件
        if(searchParam.getCatalog3Id()!=null){
            TermQueryBuilder catalogId = new TermQueryBuilder("catalogId", searchParam.getCatalog3Id());
            boolQueryBuilder.filter(catalogId);
        }
        //1.2.2、构建品牌ID过滤条件
        if(searchParam.getBrandId()!=null && searchParam.getBrandId().size() > 0) {
            TermsQueryBuilder brandId = new TermsQueryBuilder("brandId", searchParam.getBrandId());
            boolQueryBuilder.filter(brandId);
        }
        //1.2.3、构建价格区间过滤条件
        if(!StringUtils.isEmpty(searchParam.getSkuPrice())) {
            RangeQueryBuilder skuPrice = new RangeQueryBuilder("skuPrice");
            String[] s = searchParam.getSkuPrice().split("_");
            if (s.length == 2){
                skuPrice.gte(s[0]).lte(s[1]);
            }else if (s.length == 1){
                if (searchParam.getSkuPrice().startsWith("_")){
                    skuPrice.lte(s[0]);
                }

                if (searchParam.getSkuPrice().endsWith("_")){
                    skuPrice.gte(s[0]);
                }
            }
            boolQueryBuilder.filter(skuPrice);
        }
        //1.2.4、构建库存过滤条件
        if(searchParam.getHasStock()!=null) {
            TermQueryBuilder hasStock = new TermQueryBuilder("hasStock", searchParam.getHasStock()==1);
            boolQueryBuilder.filter(hasStock);
        }
        //1.2.5、构建attrs检索条件
        if(searchParam.getAttrs()!=null && searchParam.getAttrs().size()>0){


            for (String attrStr : searchParam.getAttrs()) {
                BoolQueryBuilder attrsBoolQuery = new BoolQueryBuilder();
                //attr检索条件拼装格式如下：
                //attr=1_5寸:8寸
                //attr=attrId_属性值1:属性值2:属性值3:……
                String[] s = attrStr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                attrsBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId)).
                        must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                NestedQueryBuilder attrsNested = new NestedQueryBuilder("attrs", attrsBoolQuery, null);
                boolQueryBuilder.filter(attrsNested);
            }

        }
        //bool查询条件构造完毕，传入searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);

        //2、构建排序条件
        if(!StringUtils.isEmpty(searchParam.getSort())) {
            String sort = searchParam.getSort();
            String[] s = sort.split("_");
            if ("ASC".equals(s[1].toUpperCase())) {
                searchSourceBuilder.sort(s[0], SortOrder.ASC);
            }
            if ("DESC".equals(s[1].toUpperCase())) {
                searchSourceBuilder.sort(s[0], SortOrder.DESC);
            }
        }

        //3、构建分页条件
        searchSourceBuilder.from((int) ((searchParam.getPageNum()-1)*ProductConstant.PRODUCT_PAGESIZE)).
                size(ProductConstant.PRODUCT_PAGESIZE);

        //4、构建高亮信息
        if(!StringUtils.isEmpty(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder().field("skuTitle").preTags("<b style='color:red'>").postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        //5、构建聚合dsl
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(10);
        TermsAggregationBuilder catalog_name_agg = AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(10);
        catalog_agg.subAggregation(catalog_name_agg);

        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(10);
        TermsAggregationBuilder brand_name_agg = AggregationBuilders.terms("brand_name_agg").field("brandName").size(10);
        TermsAggregationBuilder brand_img_agg = AggregationBuilders.terms("brand_img_agg").field("brandImg").size(10);
        brand_agg.subAggregation(brand_name_agg).subAggregation(brand_img_agg);

        NestedAggregationBuilder attrs_agg = AggregationBuilders.nested("attrs_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(10);
        TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(10);
        TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10);
        attr_id_agg.subAggregation(attr_name_agg).subAggregation(attr_value_agg);
        attrs_agg.subAggregation(attr_id_agg);

        searchSourceBuilder.aggregation(catalog_agg).aggregation(brand_agg).aggregation(attrs_agg);

        String s = searchSourceBuilder.toString();
        System.out.println("打印构建的dsl语句："+s);

        return searchSourceBuilder;
    }

    /**
     * 构建结果数据
     * @param searchResponse
     * @return
     */
    public SearchResult buildSearchResult(SearchResponse searchResponse,SearchParam searchParam){
        SearchResult searchResult = new SearchResult();
        SearchHits hits = searchResponse.getHits();

        List<SkuEsModelTo> products = new ArrayList<>();
        if(hits.getHits()!=null && hits.getHits().length>0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModelTo skuEsModelTo = JSON.parseObject(sourceAsString, SkuEsModelTo.class);
                if(!StringUtils.isEmpty(searchParam.getKeyword())) {
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    if (skuTitle != null) {
                        String skuTitleString = skuTitle.getFragments()[0].string();
                        skuEsModelTo.setSkuTitle(skuTitleString);
                    }
                }
                products.add(skuEsModelTo);
            }

            searchResult.setProducts(products);
        }

        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        for (Terms.Bucket bucket : brandBuckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            String keyAsString = bucket.getKeyAsString();
            brandVo.setBrandId(Long.parseLong(keyAsString));

            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_name);

            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);

            brandVos.add(brandVo);
        }
        searchResult.setBrands(brandVos);

        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));

            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);

            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);

        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrs_agg = searchResponse.getAggregations().get("attrs_agg");
        ParsedLongTerms attr_id_agg = attrs_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attr_name = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);

            List<String> attr_values = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValue(attr_values);

            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);

        long totalCount = hits.getTotalHits().value;
        searchResult.setTotal(totalCount);
        searchResult.setPageNum(searchParam.getPageNum());
        long totalPage = totalCount % ProductConstant.PRODUCT_PAGESIZE == 0 ? totalCount / ProductConstant.PRODUCT_PAGESIZE : (totalCount / ProductConstant.PRODUCT_PAGESIZE + 1);
        searchResult.setTotalPages(totalPage);
        List<Long> pageList = new ArrayList<>();
        for (long i = 1; i <= totalPage; i++) {
            pageList.add(i);
        }
        if (pageList!=null && pageList.size()>0){
            searchResult.setPageNavs(pageList);
        }

        return searchResult;
    }

    @Override
    public SearchResult search(SearchParam searchParam)  {

        //构建查询的dsl语句
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(ProductConstant.PRODUCT_INDEX);

        SearchSourceBuilder searchSourceBuilder = makerDslStatement(searchParam);

        searchRequest.source(searchSourceBuilder);
        SearchResult searchResult = new SearchResult();
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            searchResult = buildSearchResult(searchResponse, searchParam);
        } catch (IOException e) {
            e.printStackTrace();
        }



        return searchResult;
    }
}
