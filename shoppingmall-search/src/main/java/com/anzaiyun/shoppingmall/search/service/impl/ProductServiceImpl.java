package com.anzaiyun.shoppingmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.anzaiyun.common.to.es.SkuEsModelTo;
import com.anzaiyun.shoppingmall.search.config.ShoppingmallElasticSearchConfig;
import com.anzaiyun.shoppingmall.search.constant.ProductConstant;
import com.anzaiyun.shoppingmall.search.service.ProductService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public void productUp(List<SkuEsModelTo> skuEsModelList) throws IOException {

        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsModelTo skuEsModelTo : skuEsModelList) {
            IndexRequest indexRequest = new IndexRequest(ProductConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModelTo.getSkuId().toString());
            String s = JSON.toJSONString(skuEsModelTo);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, ShoppingmallElasticSearchConfig.getCommonOptions());

        System.out.println(bulkResponse);

    }
}
