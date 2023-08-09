package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品管理微服务的远程调用feign接口
 */
@FeignClient(name = "service-product", path = "/api/item")
public interface ProductFeign {
    /**
     * 查询skuInfo的信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") long skuId);

    /**
     * 根据三级分类id查询一级二级三级分类的信息
     * @param category3Id
     * @return
     */
    @GetMapping("/getBaseCategoryView/{category3Id}")
    public BaseCategoryView getBaseCategoryView(@PathVariable("category3Id") Long category3Id);

    /**
     * 查询SPU品牌列表
     *
     * @return
     */
    @GetMapping(value = "/baseTrademark/getTrademarkList")
    public Result<List<BaseTrademark>> getBaseTrademark();

    /**
     * 查询sku_image表所有信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImageList/{skuId}")
    public List<SkuImage> getSkuImageList(@PathVariable("skuId") Long skuId);

    /**
     * 根剧skuId查询sku_info表的price
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfoPrice/{skuId}")
    public BigDecimal getSkuInfoPrice(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId, skuId查询销售属性和属性值, 标识唯一的选中商品
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/getSpuSaleAttrBySpuIdBySkuId/{spuId}/{skuId}")
    public List<SpuSaleAttr> getSpuSaleAttrBySpuIdBySkuId(@PathVariable("spuId") Long spuId,
                                                          @PathVariable("skuId") Long skuId);

    /**
     * 根据spu的id查询sku的销售属性键值对
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuSaleAttrValueKeys/{spuId}")
    public Map getSkuSaleAttrValueKeys(@PathVariable("spuId") Long spuId);

    /**
     * 根据skuId查询品牌信息
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseTrademark/{skuId}")
    public BaseTrademark getBaseTrademark(@PathVariable("skuId") Long skuId);

    /**
     * 根据skuId查询销售属性信息
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfoBySkuId/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfoBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 扣减库存
     * @param map
     * @return
     */
    @GetMapping(value = "/decountStock")
    public Boolean decountStock(@RequestParam Map<String,String> map);

    /**
     * 回滚库存
     * @param map
     * @return
     */
    @GetMapping(value = "/rollbackStock")
    public Boolean rollbackStock(@RequestParam Map<String,String> map);
}
