package com.anzaiyun.shoppingmall.search;

import com.alibaba.fastjson.JSON;
import com.anzaiyun.shoppingmall.search.config.ShoppingmallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ShoppingmallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ShoppingmallElasticSearchConfig searchConfig;

    @Test
    void contextLoads() {
        System.out.println(client);
    }

    /**
     *测试创建索引index
     */
    @Test
    public void testCreateIndex() throws IOException {
        RestHighLevelClient client = searchConfig.esRestClient();

        CreateIndexRequest javaTestRequest = new CreateIndexRequest("javatest001");

        //手工拼接json字符串做入参
//        javaTestRequest.mapping(
//                "{\n" +
//                        "  \"properties\": {\n" +
//                        "    \"message\": {\n" +
//                        "      \"type\": \"text\"\n" +
//                        "    }\n" +
//                        "  }\n" +
//                        "}",
//                XContentType.JSON);

        //采用map的方式传参
//        Map<String, Object> message = new HashMap<>();
//        message.put("type","text");
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("message",message);
//        Map<String, Object> mapping = new HashMap<>();
//        mapping.put("properties",properties);
//        javaTestRequest.mapping(mapping);

        //采用XContentBuilder格式
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("message");
                {
                    builder.field("type","text");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();

        javaTestRequest.mapping(builder);

        client.indices().create(javaTestRequest, RequestOptions.DEFAULT);

    }


    /**
     * 测试保存数据
     * @throws IOException
     */
    @Test
    void testIndex() throws IOException {
        IndexRequest indexRequest = new IndexRequest("uesrs");

        indexRequest.id("1");
//        indexRequest.source("username","zhangsan","gender","M","age",23);
        User user = new User();
        user.setUsername("zhangsan");
        user.setAge(18);
        user.setGender("M");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(index);
    }

    @Test
    void testSearchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        //配置dsl
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构造检索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","Street"));
        //按照年龄值进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("age_agg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        //计算平均薪资
        AvgAggregationBuilder balanceAgg = AggregationBuilders.avg("balance_agg").field("balance");
        searchSourceBuilder.aggregation(balanceAgg);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(searchResponse.toString());
        //解析结果
        //获取所有的命中记录
        System.out.println("获取所有的命中记录");
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hitsList = hits.getHits();
        for(SearchHit hit:hitsList){
            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);

        }


        //获取分析信息
        System.out.println("获取分析信息");
        Aggregations aggregations = searchResponse.getAggregations();
//        for(Aggregation aggregation:aggregations.asList()){
//            String name = aggregation.getName();
//            System.out.println(name);
//        }
        Terms ageAggResult = aggregations.get("age_agg");
        for (Terms.Bucket bucket : ageAggResult.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄："+keyAsString);
        }

        Avg avg = aggregations.get("balance_agg");
        System.out.println("平均薪资："+avg.getValue());

    }

    @Data
    class User{
        private String username;
        private String gender;
        private Integer age;
    }

    @Test
    public void testFor(){

        int i=1;
        int j=0;

        if (i==1 && (j=i)==2){
            System.out.println(123);
        }

        System.out.println(j);
    }


}
