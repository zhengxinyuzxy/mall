package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.atguigu.gmall.common.constant.PageConst;
import com.atguigu.gmall.list.service.TrySearchService;
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

import java.util.*;

@Service
public class TrySearchServiceImpl implements TrySearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> searchs(Map<String, String> searchMap) {
        // 参数校验
        if (searchMap.isEmpty()) {
            return null;
        }
        // 查询条件拼接
        SearchRequest searchRequest = getQueryParam(searchMap);
        try {
            // 执行查询，获取查询结果
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 解析结果，包装结果
            Map<String, Object> result = getSearchResponseResult(search);
            // 添加页码
            result.put("page", getPageValue(searchMap));
            // 返回结果
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private SearchRequest getQueryParam(Map<String, String> searchMap) {
        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 条件拆分
        // 构建boolQuery查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 关键字查询
        String keywords = searchMap.get("keywords");
        if (!org.apache.commons.lang3.StringUtils.isEmpty(keywords)) {
//            searchSourceBuilder.query(QueryBuilders.matchQuery("title", keywords));
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", keywords));
        }
        // 品牌的查询条件
        String tradeMake = searchMap.get("tradeMake");
        if (!org.apache.commons.lang3.StringUtils.isEmpty(tradeMake)) {
            String[] split = tradeMake.split(":");
            // 根据品牌id查询
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", split[0]));
            // 根据品牌名称查询
//            boolQueryBuilder.must(QueryBuilders.termQuery("tmName", split[1]));
        }

        // 平台属性的查询条件,参数：attr_运行内存=3:4GB
        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            String attr = entry.getKey();
            if (attr.startsWith("attr_")) {
                String value = entry.getValue();
                String[] split = value.split(":");
                BoolQueryBuilder nestBoolQueryBuilder = QueryBuilders.boolQuery();
                nestBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                nestBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                boolQueryBuilder.must(QueryBuilders.nestedQuery("attrs", nestBoolQueryBuilder, ScoreMode.None));
            }
        }

        // 价格的查询条件
        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {
            String replacePrice = price.replace("元", "").replace("以上", "");
            String[] split = replacePrice.split("-");
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            if (split.length > 1) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        // 分页
        // 每页显示多少条
        searchSourceBuilder.size(PageConst.ES_PAGE_SIZE);
        Integer page = getPageValue(searchMap);
        // 当前页从多少条开始
        searchSourceBuilder.from((page - 1) * PageConst.ES_PAGE_SIZE);
        // 排序
        String sortFiled = searchMap.get("sortFiled");
        if (!StringUtils.isEmpty(sortFiled)) {
            String sortRule = searchMap.get("sortRule");
            if (!StringUtils.isEmpty(sortRule)) {
                searchSourceBuilder.sort(sortFiled, SortOrder.valueOf(sortRule));
            } else {
                searchSourceBuilder.sort(sortFiled, SortOrder.DESC);
            }
        }

        // 高亮数据的查询
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color:red'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);

        // 所有的查询条件
        searchSourceBuilder.query(boolQueryBuilder);
        // 品牌的聚合查询条件
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("assTmId").field("tmId")
                        .subAggregation(AggregationBuilders.terms("assTmName").field("tmName"))
                        .subAggregation(AggregationBuilders.terms("assTmLogoUrl").field("tmLogoUrl"))
                        .size(10000));

        // 平台属性的聚合查询条件
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("aggAttrs", "attrs")
                        .subAggregation(
                                AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                                        .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                                        .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                                        .size(10000)));
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    private Integer getPageValue(Map<String, String> searchMap) {
        String page = searchMap.get("page");
        try {
            return Integer.parseInt(page) > 0 ? Integer.parseInt(page) : 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }


    private Map<String, Object> getSearchResponseResult(SearchResponse search) {
        Map<String, Object> result = new HashMap<>();
        SearchHits hits = search.getHits();
        // 计算条数，页数
        long totalHits = hits.getTotalHits();
        result.put("total", totalHits);
        result.put("size", PageConst.ES_PAGE_SIZE);
        Iterator<SearchHit> iterator = hits.iterator();
        List<Goods> goodsList = new ArrayList<>();
        while (iterator.hasNext()) {
            SearchHit next = iterator.next();
            String sourceAsString = next.getSourceAsString();
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
        result.put("goodsList", goodsList);

        Map<String, Aggregation> aggregationMap = search.getAggregations().asMap();
        List<SearchResponseTmVo> tmResult = getTmResult(aggregationMap);
        result.put("tmResult", tmResult);

        List<SearchResponseAttrVo> attrsResult = getAttrsResult(aggregationMap);
        result.put("attrsResult", attrsResult);

        return result;
    }

    private List<SearchResponseAttrVo> getAttrsResult(Map<String, Aggregation> aggregationMap) {
        ParsedNested aggAttrs = (ParsedNested) aggregationMap.get("aggAttrs");
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        List<SearchResponseAttrVo> attrVoList = new ArrayList<>();
        for (Terms.Bucket bucket : aggAttrId.getBuckets()) {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            long attrId = bucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            ParsedStringTerms aggAttrName = bucket.getAggregations().get("aggAttrName");
            List<? extends Terms.Bucket> attrNameBuckets = aggAttrName.getBuckets();
            if (!attrNameBuckets.isEmpty()) {
                String keyAsString = attrNameBuckets.get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(keyAsString);
            }
            ParsedStringTerms aggAttrValue = bucket.getAggregations().get("aggAttrValue");
            List<String> attrvalueList = new ArrayList<>();
            for (Terms.Bucket aggAttrValueBucket : aggAttrValue.getBuckets()) {
                String keyAsString = aggAttrValueBucket.getKeyAsString();
                attrvalueList.add(keyAsString);
            }
            searchResponseAttrVo.setAttrValueList(attrvalueList);
            attrVoList.add(searchResponseAttrVo);
        }
        return attrVoList;
    }

    private List<SearchResponseTmVo> getTmResult(Map<String, Aggregation> tmMap) {
        ParsedLongTerms assTmId = (ParsedLongTerms) tmMap.get("assTmId");
        List<SearchResponseTmVo> tmVos = new ArrayList<>();
        for (Terms.Bucket bucket : assTmId.getBuckets()) {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);

            ParsedStringTerms assTmName = bucket.getAggregations().get("assTmName");
            List<? extends Terms.Bucket> tmNameBuckets = assTmName.getBuckets();
            if (!tmNameBuckets.isEmpty()) {
                String tmName = assTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);

            }

            ParsedStringTerms assTmLogoUrl = bucket.getAggregations().get("assTmLogoUrl");
            List<? extends Terms.Bucket> tmLogoUrlBuckets = assTmLogoUrl.getBuckets();
            if (!tmLogoUrlBuckets.isEmpty()) {
                String tmLogoUrl = assTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }

            tmVos.add(searchResponseTmVo);
        }
        return tmVos;
    }
}
