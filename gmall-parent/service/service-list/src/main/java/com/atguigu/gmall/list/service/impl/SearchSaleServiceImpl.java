package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchSaleService;
import com.atguigu.gmall.model.list.Goods;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

public class SearchSaleServiceImpl implements SearchSaleService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品搜索
     *
     * @param searchData
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchData) {
        // 参数校验
        if (searchData == null) {
            // 不存在没有搜索条件的情况，所以直接返回就可以
            return null;
        }

        SearchRequest searchRequest = builderQueryParam(searchData);
        try {
            // 执行查询，获取结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析结果
            Map<String, Object> searchResult = getSearchResult(searchResponse);
            // 返回结果
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    // 拼接查询条件
    private SearchRequest builderQueryParam(Map<String, String> searchData) {
        SearchRequest searchRequest = new SearchRequest("goods_zhengxinyu");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        String keyWords = searchData.get("keyWords");
        if (!StringUtils.isEmpty(keyWords)) {
            builder.query(QueryBuilders.matchQuery("title", keyWords));

        }
        searchRequest.source(builder);
        return searchRequest;

    }

    // 解析结果
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        // 初始化放回结果
        HashMap<String, Object> result = new HashMap<>();
        // 获取命中结果
        SearchHits hits = searchResponse.getHits();
        // 初始化查询结果类型
        List<Goods> goodsList = new ArrayList<>();
        //获取迭代
        Iterator<SearchHit> iterator = hits.iterator();
        // 遍历
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            // 获取String类型的查询结果
            String sourceAsString = next.getSourceAsString();
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);

        }
        // 存储查询商品的lieb
        result.put("goodslist", goodsList);
        return result;
    }
}
