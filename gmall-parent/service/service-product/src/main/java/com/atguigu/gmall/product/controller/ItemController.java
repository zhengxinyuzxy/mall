package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品详情页的内部api接口
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 查询skuInfo的信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "getSkuInfo:")
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") long skuId) {
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 从redis或数据库查询skuInfo的信息， redis单点情况
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfoFromRedisOrDb/{skuId}")
    public SkuInfo getSkuInfoFromRedisOrDb(@PathVariable("skuId") long skuId) {
        return itemService.getSkuInfoFromRedisOrDb(skuId);
    }

    /**
     * 使用redisson从redis或数据库查询skuInfo的信息， redis集群情况
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfoFromRedisson/{skuId}")
    public SkuInfo getSkuInfoFromRedisson(@PathVariable("skuId") long skuId) {
        return itemService.getSkuInfoFromRedisson(skuId);
    }

    /**
     * 根据三级分类id查询一级二级三级分类的信息
     * @param category3Id
     * @return
     */
    @GmallCache(prefix = "getBaseCategoryView:")
    @GetMapping("/getBaseCategoryView/{category3Id}")
    public BaseCategoryView getBaseCategoryView(@PathVariable("category3Id") Long category3Id) {
        return itemService.getBaseCategoryView(category3Id);
    }

    /**
     * 查询sku_image表所有信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "getSkuImageList:")
    @GetMapping("/getSkuImageList/{skuId}")
    public List<SkuImage> getSkuImageList(@PathVariable("skuId") Long skuId) {
        return itemService.getSkuImageList(skuId);
    }

    /**
     * 根剧skuId查询sku_info表的price
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "getSkuInfoPrice:")
    @GetMapping("/getSkuInfoPrice/{skuId}")
    public BigDecimal getSkuInfoPrice(@PathVariable("skuId") Long skuId) {
        return itemService.getSkuInfoPrice(skuId);
    }

    /**
     * 根据spuId, skuId查询销售属性和属性值, 标识唯一的选中商品
     * @param spuId
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "getSpuSaleAttrBySpuIdBySkuId:")
    @GetMapping("/getSpuSaleAttrBySpuIdBySkuId/{spuId}/{skuId}")
    public List<SpuSaleAttr> getSpuSaleAttrBySpuIdBySkuId(@PathVariable("spuId") Long spuId,
                                                          @PathVariable("skuId") Long skuId) {
        return itemService.getSpuSaleAttrBySpuIdBySkuId(spuId, skuId);
    }

    /**
     * 根据spu的id查询sku的销售属性键值对
     * @param spuId
     * @return
     */
    @GmallCache(prefix = "getSkuSaleAttrValueKeys:")
    @GetMapping("/getSkuSaleAttrValueKeys/{spuId}")
    public Map getSkuSaleAttrValueKeys(@PathVariable("spuId") Long spuId) {
        return itemService.getSkuSaleAttrValueKeys(spuId);
    }

    /**
     * 根据skuId查询品牌信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "getBaseTrademark:")
    @GetMapping("/getBaseTrademark/{id}")
    public BaseTrademark getBaseTrademark(@PathVariable("id") Long id) {
        return itemService.getBaseTrademark(id);
    }

    /**
     * 根据skuId查询平台属性信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "getBaseAttrInfoBySkuId:")
    @GetMapping("/getBaseAttrInfoBySkuId/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(@PathVariable("skuId") Long skuId) {
        return itemService.getBaseAttrInfoBySkuId(skuId);
    }

    /**
     * 扣减库存
     * @param map
     * @return
     */
    @GetMapping(value = "/decountStock")
    public Boolean decountStock(@RequestParam Map<String, String> map) {
        return itemService.decountStock(map);
    }

    /**
     * 回滚库存
     * @param map
     * @return
     */
    @GetMapping(value = "/rollbackStock")
    public Boolean rollbackStock(@RequestParam Map<String, String> map) {
        return itemService.decountStock(map);
    }
}
