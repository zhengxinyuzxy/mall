package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.list.service.TrySearchService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/list")
public class ListController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private ListService listService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private TrySearchService trySearchService;

    /**
     * 创建索引和类型映射
     */
    @GetMapping("/create")
    public Result createIndexAndMapper() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 商品的上架
     */
    @GetMapping("/upper/{skuId}")
    public void upper(@PathVariable("skuId") Long skuId) {
        listService.upper(skuId);
    }

    /**
     * 商品的下架
     */
    @GetMapping("/down/{skuId}")
    public void down(@PathVariable("skuId") Long skuId) {
        listService.down(skuId);

    }

    /**
     * 热度值自增
     * @param skuId
     */
    @GetMapping("/addScore/{skuId}")
    public void addScore(@PathVariable("skuId") Long skuId) {
        listService.addScore(skuId);
    }

    /**
     * 商品的搜索
     * @param SearchMap
     * @return
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam Map<String, String> SearchMap) {
        return searchService.search(SearchMap);
    }

    //    自测---Todo
    @GetMapping("/searchs")
    public Map<String, Object> searchs(@RequestParam Map<String, String> searchMap) {
        return trySearchService.searchs(searchMap);
    }

}
