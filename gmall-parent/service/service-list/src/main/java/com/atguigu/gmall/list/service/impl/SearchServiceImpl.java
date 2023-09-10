package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.atguigu.gmall.common.constant.PageConst;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

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
        // 参数校验,一般搜索不会出现异常，不选搜索条件有默认值搜索
        if (searchData == null) {
            return null;
        }
        // 拼接条件
        SearchRequest searchRequest = builderQueryParam(searchData);
        try {
            // 执行条件查询，获取结果
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析结果,单独写一个解析结果的方法，减少查询和解析的压力
            Map<String, Object> searchResult = getSearchResult(searchResponse);
            searchResult.put("page", getPage(searchData));
            // 返回结果
            return searchResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 拼接查询条件
     *
     * @param searchData
     * @return
     */
    private SearchRequest builderQueryParam(Map<String, String> searchData) {
        // 初始化，指定查询的索引
        SearchRequest searchRequest = new SearchRequest("goods");
        // 查询条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();

        // 构建Bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 根据关键字进行查询
        // 获取关键字
        String keywords = searchData.get("keywords");
        // 判断参数是否满足
        if (!StringUtils.isEmpty(keywords)) {
            // 构造器选择查询的方式matchQuery,查询的域是title
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", keywords));
            // 因为会覆盖，使用bool查询
//            SearchSourceBuilder title = builder.query(QueryBuilders.matchQuery("title", keywords));
        }

        // 根据品牌查询
        String trademark = searchData.get("tradeMark");
        if (!StringUtils.isEmpty(trademark)) {
            String[] split = trademark.split(":");
            // 根据品牌的id进行查询
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
            // 根据品牌的名字进行查询
            boolQueryBuilder.must(QueryBuilders.termQuery("tmName", split[1]));
        }

        // 根据平台属性查询
        for (Map.Entry<String, String> stringEntry : searchData.entrySet()) {
            String key = stringEntry.getKey();
            if (key.startsWith("attr_")) {
                // 平台属性的名字
//                key = key.replace("attr_", "");
                String value = stringEntry.getValue();
                String[] split = value.split(":");
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None));
            }
        }

        // 根据价格查询
        String price = searchData.get("price");
        // 参数校验，防止空指针异常NullPointerException
        if (!StringUtils.isEmpty(price)) {
            // 对price进行数据处理
            price = price
                    .replace("以上", "")
                    .replace("元", "");
            String[] split = price.split("-");

            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            if (split.length > 1) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }

        // 分页和排序
        builder.size(PageConst.ES_PAGE_SIZE);
        Integer page = getPage(searchData);
        builder.from((page - 1) * PageConst.ES_PAGE_SIZE);

        // 排序的实现
        String sortFiled = searchData.get("sortFiled");
        if (!StringUtils.isEmpty(sortFiled)) {
            String sortRule = searchData.get("sortRule");
            if (!StringUtils.isEmpty(sortRule)) {
                builder.sort(sortFiled, SortOrder.valueOf(sortRule));
            } else {
                builder.sort(sortFiled, SortOrder.DESC);
            }
        }

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color:red'>");
        highlightBuilder.postTags("</font");
        builder.highlighter(highlightBuilder);

        // 设置查询条件
        builder.query(boolQueryBuilder);
        // 在关键字的基础上对品牌聚合查询
        builder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                        .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
                        .size(10000)
        );
        // 对平台属性进行聚合查询
        builder.aggregation(
                AggregationBuilders.nested("aggAttrs", "attrs")
                        .subAggregation(
                                AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                        .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                        .size(10000)
                        )
        );

        // 执行条件拼接
        searchRequest.source(builder);
        return searchRequest;
    }

    /**
     * 页码的数据处理
     *
     * @param searchData
     */
    private Integer getPage(Map<String, String> searchData) {
        String page = searchData.get("page");
        try {
            return Integer.parseInt(page) > 0 ? Integer.parseInt(page) : 1;
        } catch (Exception e) {
            return 1;
        }

    }

    /**
     * 解析结果
     *
     * @param searchResponse
     */
    private Map<String, Object> getSearchResult(SearchResponse searchResponse) {
        // 返回结果初始化,对List的结果转换为Map
        HashMap<String, Object> result = new HashMap<>();
        // 获取命中的结果
        SearchHits hits = searchResponse.getHits();
        // 获取总数量
        long totalHits = hits.getTotalHits();
        result.put("total", totalHits);
        result.put("size", PageConst.ES_PAGE_SIZE);
        // 初始化反序列化的商品列表
        List<Goods> goodsList = new ArrayList<>();
        // 获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        // 遍历,获取命中的商品数据
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            // 获取json类型的商品列表
            String sourceAsString = next.getSourceAsString();
            // 对数据进行反序列化为对象
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);

            // 获取需要高亮的数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if (highlightField != null) {
                Text[] fragments = highlightField.getFragments();
                if (fragments != null && fragments.length > 0) {
                    String title = "";
                    for (Text fragment : fragments) {
                        title += fragment;
                    }
                    goods.setTitle(title);
                }
            }
            goodsList.add(goods);
        }
        // 保存解析过的商品列表的数据
        result.put("goodsList", goodsList);
        // 获取所有的聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        // 获取所有聚合的map
        Map<String, Aggregation> aggregationsMap = aggregations.getAsMap();

        // 获取品牌的聚合结果
        List<SearchResponseTmVo> trademarResult = getTrademarkResult(aggregationsMap);
        // 保存解析过的品牌的数据
        result.put("trademarkResultList", trademarResult);

        //获取平台属性的聚合结果
        List<SearchResponseAttrVo> baseAttrResult = getBaseAttrResult(aggregationsMap);
        // 保存解析过的平台的数据
        result.put("baseAttrResultList", baseAttrResult);

        // 拿到商品的列表
        return result;
    }

    /**
     * 获取平台属性的聚合结果
     *
     * @param aggregationsMap
     */
    private List<SearchResponseAttrVo> getBaseAttrResult(Map<String, Aggregation> aggregationsMap) {
        ParsedNested aggAttrs = (ParsedNested) aggregationsMap.get("aggAttrs");
        // 平台属性聚合结果初始化列表
        List<SearchResponseAttrVo> searchResponseAttrVos = new ArrayList<>();

        ParsedLongTerms aggAttrIds = aggAttrs.getAggregations().get("aggAttrId");
        for (Terms.Bucket bucket : aggAttrIds.getBuckets()) {
            // 平台属性初始化包装对象
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            // 获取聚合后的平台属性id
            long attrId = bucket.getKeyAsNumber().longValue();
            // 设置包装对象属性
            searchResponseAttrVo.setAttrId(attrId);

            // 获取聚合后的平台属性名
            ParsedStringTerms aggAttrName = bucket.getAggregations().get("aggAttrName");
            List<? extends Terms.Bucket> aggAttrNameBuckets = aggAttrName.getBuckets();
            if (!aggAttrNameBuckets.isEmpty()) {
                String attrName = aggAttrNameBuckets.get(0).getKeyAsString();
                // 设置包装对象属性
                searchResponseAttrVo.setAttrName(attrName);
            }
            // 获取聚合后的平台属性值
            ParsedStringTerms aggAttrValues = bucket.getAggregations().get("aggAttrValue");
            // 平台属性值的初始化
            List<String> attrValues = new ArrayList<>();
            List<? extends Terms.Bucket> aggAttrValuesBuckets = aggAttrValues.getBuckets();
            for (Terms.Bucket aggAttrValueBucket : aggAttrValuesBuckets) {
                String attrValue = aggAttrValueBucket.getKeyAsString();
                attrValues.add(attrValue);
            }
            // 设置包装对象属性
            searchResponseAttrVo.setAttrValueList(attrValues);
            searchResponseAttrVos.add(searchResponseAttrVo);
        }
        return searchResponseAttrVos;
    }

    /**
     * 获取品牌的聚合结果
     *
     * @param aggregationsMap
     * @return
     */
    private List<SearchResponseTmVo> getTrademarkResult(Map<String, Aggregation> aggregationsMap) {
        // 获取品牌的聚合结果
        ParsedLongTerms aggTmId = (ParsedLongTerms) aggregationsMap.get("aggTmId");
        List<SearchResponseTmVo> searchResponseTmVos = new ArrayList<>();

        for (Terms.Bucket bucket : aggTmId.getBuckets()) {
            // 初始化品牌
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            // 获取品牌的Id
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            // 获取品牌的名字
            ParsedStringTerms aggTmName = bucket.getAggregations().get("aggTmName");
            // 因为图片名称和Logo可能会不存在，避免索引下标越界异常，进行判断
            List<? extends Terms.Bucket> tmNameBuckets = aggTmName.getBuckets();
            if (!tmNameBuckets.isEmpty()) {
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            // 获取品牌的Logo
            ParsedStringTerms aggTmLogoUrl = bucket.getAggregations().get("aggTmLogoUrl");
            List<? extends Terms.Bucket> tmLogoUrlBuckets = aggTmLogoUrl.getBuckets();
            if (!tmLogoUrlBuckets.isEmpty()) {
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            searchResponseTmVos.add(searchResponseTmVo);
        }
        return searchResponseTmVos;
    }
}
