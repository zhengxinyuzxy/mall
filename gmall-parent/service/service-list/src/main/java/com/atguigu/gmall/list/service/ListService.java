package com.atguigu.gmall.list.service;

public interface ListService {

    /**
     * 商品的上架
     * @param skuId
     */
    public void upper(Long skuId);

    /**
     * 商品的下架
     * @param skuId
     */
    public void down(Long skuId);

    /**
     * 增加热度
     * @param skuId
     */
    public void addScore(Long skuId);
}
