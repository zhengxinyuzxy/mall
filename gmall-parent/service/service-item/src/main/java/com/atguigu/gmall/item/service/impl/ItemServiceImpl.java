package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 商品详情的servie
 */
@Service
public class ItemServiceImpl implements ItemService {

    //    @Autowired
    @Resource
    private ProductFeign productFeign;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 获取商品详情的信息
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getSkuItem(Long skuId) {
        // 参数校验
        if (skuId == null) {
            throw new RuntimeException("商品不存在！");
        }

        // 创建map存放查询的不同类型结果
        Map<String, Object> map = new HashMap<>();

        // 添加异步编排
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
            // 查询SkuInfo信息
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);

            // 判断skuInfo是否真实存在数据
            if (skuInfo == null || skuInfo.getId() == null) {
                throw new RuntimeException("商品不存在!");
            }

            // 向map中添加skuInfo
            map.put("skuInfo", skuInfo);
            return skuInfo;
        }, threadPoolExecutor);


        // 获取SKU的平台信息, 查询一级二级三级分类的id和name
        CompletableFuture<Void> future2 = future1.thenAcceptAsync((skuInfo) -> {
            // 判断skuInfo是否真实存在数据
            if (skuInfo == null || skuInfo.getId() == null) {
                throw new RuntimeException("商品不存在!");
            }

            BaseCategoryView baseCategoryView = productFeign.getBaseCategoryView(skuInfo.getCategory3Id());
            map.put("baseCategoryView", baseCategoryView);
        }, threadPoolExecutor);


        // 获取SkuImag图片列表
        CompletableFuture<Void> future3 = future1.thenAcceptAsync((skuInfo) -> {
            // 判断skuInfo是否真实存在数据
            if (skuInfo == null || skuInfo.getId() == null) {
                throw new RuntimeException("商品不存在!");
            }

            List<SkuImage> skuImageList = productFeign.getSkuImageList(skuInfo.getId());
            map.put("skuImageList", skuImageList);
        }, threadPoolExecutor);

        // 获取SKU的价格信息
        CompletableFuture<Void> future4 = future1.thenAcceptAsync((skuInfo) -> {
            // 判断skuInfo是否真实存在数据
            if (skuInfo == null || skuInfo.getId() == null) {
                throw new RuntimeException("商品不存在!");
            }

            BigDecimal skuInfoPrice = productFeign.getSkuInfoPrice(skuInfo.getId());
            map.put("skuInfoPrice", skuInfoPrice);
        }, threadPoolExecutor);


        // 获取SkuSaleAttr销售属性和属性值信息, 标记所属哪一列SKU
        CompletableFuture<Void> future5 = future1.thenAcceptAsync((skuInfo) -> {
            // 判断skuInfo是否真实存在数据
            if (skuInfo == null || skuInfo.getId() == null) {
                throw new RuntimeException("商品不存在!");
            }

            List<SpuSaleAttr> spuSaleAttrList = productFeign.getSpuSaleAttrBySpuIdBySkuId(skuInfo.getSpuId(), skuInfo.getId());
            map.put("spuSaleAttrList", spuSaleAttrList);
        }, threadPoolExecutor);


        // 根据spuId查询skuSaleAttrValueKeys键值对
        CompletableFuture<Void> future6 = future1.thenAcceptAsync((skuInfo) -> {
            // 判断skuInfo是否真实存在数据
            if (skuInfo == null || skuInfo.getId() == null) {
                throw new RuntimeException("商品不存在!");
            }

            Map mapKeys = productFeign.getSkuSaleAttrValueKeys(skuInfo.getSpuId());
            map.put("mapKeys", mapKeys);
        }, threadPoolExecutor);

        CompletableFuture.allOf(future1, future2, future3, future4, future5, future6).join();
        // 返回结果
        return map;
    }

}
