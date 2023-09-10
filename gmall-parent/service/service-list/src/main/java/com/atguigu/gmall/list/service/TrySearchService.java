package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * 商品的搜索
 */
public interface TrySearchService {
    /**
     * 商品搜索
     * @param searchMap
     * @return
     */
    Map<String, Object> searchs(Map<String, String> searchMap);
}
