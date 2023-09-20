package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品详情页的内部接口
 */
public interface ItemService {

    /**
     * 查询关于sku_info表的所有信息
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据redisorDb查询关于sku_info表的所有信息
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfoFromRedisOrDb(Long skuId);

    /**
     * 使用redisson从redis和数据库中查询skuInfo的信息，redis集群情况
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfoFromRedisson(Long skuId);

    /**
     * 根据三级分类查询一级二级三级分类信息
     * @param category3Id
     * @return
     */
    public BaseCategoryView getBaseCategoryView(Long category3Id);

    /**
     * 查询图片列表
     * @param skuId
     * @return
     */
    public List<SkuImage> getSkuImageList(Long skuId);

    /**
     * 根据skuId查询sku_info表的price价格
     * @param skuId
     * @return
     */
    public BigDecimal getSkuInfoPrice(Long skuId);

    /**
     * 根据spuId, skuId查询销售属性和属性值, 标识唯一的选中商品
     * @param spuId
     * @param skuId
     * @return
     */
    public List<SpuSaleAttr> getSpuSaleAttrBySpuIdBySkuId(Long spuId, Long skuId);

    /**
     * 根据spu的id查询sku的销售属性键值对
     * @param spuId
     * @return
     */
    public Map getSkuSaleAttrValueKeys(Long spuId);

    /**
     * 根据skuId查询品牌信息
     * @param skuId
     * @return
     */
    public BaseTrademark getBaseTrademark(Long id);

    /**
     * 根据skuId查询平台属性信息
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId);

    /**
     * 扣减库存
     * @param map
     */
    public Boolean decountStock(Map<String, String> map);

    /**
     * 回滚库存
     * @param map
     */
    public Boolean rollbackStock(Map<String, String> map);
}
