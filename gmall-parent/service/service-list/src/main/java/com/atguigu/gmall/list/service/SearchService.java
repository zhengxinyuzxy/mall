package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * 搜索
 */
public interface SearchService {

    /**
     * 商品搜索
     * @param searchData
     * @return
     */
    public Map<String, Object> search(Map<String, String> searchData);
}
