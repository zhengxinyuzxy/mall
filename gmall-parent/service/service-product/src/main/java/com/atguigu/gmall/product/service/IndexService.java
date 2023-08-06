package com.atguigu.gmall.product.service;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 商品详情页的一级二级三级数据的相关接口
 */
public interface IndexService {

    /**
     * 查询所有首页的一级二级三级信息
     *
     * @return
     */
    public List<JSONObject> getIndexCategory();
}
