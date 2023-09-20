package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private ProductFeign productFeign;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * es商品的上架
     * @param skuId
     */
    @Override
    public void upper(Long skuId) {
//        // 参数校验
//        if (skuId == null) {
//            throw new RuntimeException("参数异常！");
//        }
//        Goods goods = new Goods();
//
//        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
//            // 查询skuInfo的信息
//            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
//
//            if (skuInfo == null || skuInfo.getId() == null) {
//                throw new RuntimeException("参数错误！");
//            }
//
//            // 查询的skUinfo转换为goods
//            goods.setId(skuInfo.getId());
//            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
//            goods.setTitle(skuInfo.getSkuName());
//            goods.setPrice(skuInfo.getPrice().doubleValue());
//            goods.setCreateTime(new Date());
//            return skuInfo;
//        },threadPoolExecutor);
//
//
//        // 获取品牌的信息--TODO
//        CompletableFuture<Void> future2 = future1.thenAcceptAsync((skuInfo) -> {
//            if (skuInfo == null || skuInfo.getId() == null) {
//                throw new RuntimeException("参数错误！");
//            }
//
//            BaseTrademark baseTrademark =
//                    productFeign.getBaseTrademark(skuInfo.getTmId());
//
//            goods.setTmId(baseTrademark.getId());
//            goods.setTmName(baseTrademark.getTmName());
//            goods.setTmLogoUrl(baseTrademark.getLogoUrl());
//        },threadPoolExecutor);
//
//        CompletableFuture<Void> future3 = future1.thenAcceptAsync((skuInfo) -> {
//            BaseCategoryView baseCategoryView =
//                    productFeign.getBaseCategoryView(skuInfo.getCategory3Id());
//
//            goods.setCategory1Id(baseCategoryView.getCategory1Id());
//            goods.setCategory1Name(baseCategoryView.getCategory1Name());
//            goods.setCategory2Id(baseCategoryView.getCategory2Id());
//            goods.setCategory2Name(baseCategoryView.getCategory2Name());
//            goods.setCategory3Id(baseCategoryView.getCategory3Id());
//            goods.setCategory3Name(baseCategoryView.getCategory3Name());
//        },threadPoolExecutor);
//
//
//        // 设置平台属性 --TODO
//        CompletableFuture<Void> future4 = future1.thenAcceptAsync((skuInfo) -> {
//            if (skuInfo == null || skuInfo.getId() == null) {
//                throw new RuntimeException("参数错误！");
//            }
//
//            List<BaseAttrInfo> baseAttrInfoList =
//                    productFeign.getBaseAttrInfoBySkuId(skuInfo.getId());
//
//            // 参数校验
//            if (baseAttrInfoList.isEmpty()) {
//                return;
//            }
//            List<SearchAttr> searchAttrList = baseAttrInfoList.stream().map(baseAttrInfo -> {
//                SearchAttr searchAttr = new SearchAttr();
//                searchAttr.setAttrId(baseAttrInfo.getId());
//                searchAttr.setAttrName(baseAttrInfo.getAttrName());
//                searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
//                return searchAttr;
//
//            }).collect(Collectors.toList());
//
//            goods.setAttrs(searchAttrList);
//        },threadPoolExecutor);
//
//        // 设置热点值--TODO
//
//        // 存入redis中
//        CompletableFuture.allOf(future1,future2,future3,future4).join();
//        goodsDao.save(goods);
        // 参数验证
        if (skuId == null) {
            throw new RuntimeException("参数异常！");
        }
        Goods goods = new Goods();
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("参数异常");
        }
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        goods.setTmId(skuInfo.getTmId());

        BaseTrademark baseTrademark = productFeign.getBaseTrademark(skuInfo.getTmId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());

        BaseCategoryView baseCategoryView = productFeign.getBaseCategoryView(skuInfo.getCategory3Id());
        goods.setCategory1Id(baseCategoryView.getCategory1Id());
        goods.setCategory2Id(baseCategoryView.getCategory2Id());
        goods.setCategory3Id(baseCategoryView.getCategory3Id());
        goods.setCategory1Name(baseCategoryView.getCategory1Name());
        goods.setCategory2Name(baseCategoryView.getCategory2Name());
        goods.setCategory3Name(baseCategoryView.getCategory3Name());

        List<BaseAttrInfo> baseAttrInfoList = productFeign.getBaseAttrInfoBySkuId(skuId);
        List<SearchAttr> searchAttrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(searchAttrs);
        // 写入es
        goodsDao.save(goods);
    }

    /**
     * es商品的下架
     * @param skuId
     */
    @Override
    public void down(Long skuId) {
        // 参数校验
        if (skuId == null) {
            throw new RuntimeException("商品不存在！");
        }
        // 删除es中商品
        goodsDao.deleteById(skuId);

    }

    /**
     * 增加热度
     * @param skuId
     */
    @Override
    public void addScore(Long skuId) {
        // 参数校验
        if (skuId == null) {
            throw new RuntimeException("参数错误！");
        }
        // 查询es中是否存在
        Goods goods = goodsDao.findById(skuId).get();
        if (goods == null || goods.getId() == null) {
            throw new RuntimeException("参数校验！");
        }

        // 因为es的压力太多，转为向redis中存
        // 修改热度值
        Double hotScore = redisTemplate.opsForZSet().incrementScore("hotScore", "goods" + skuId, 1);

        if (hotScore % 10 == 0) {
            goods.setHotScore(hotScore.longValue());
            // 保存
            goodsDao.save(goods);
        }
    }
}
