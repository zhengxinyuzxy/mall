package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理控制台的controller层
 */
//@Api(value = "ManageController", tags = "分类与属性管理控制层")
@RestController
@RequestMapping(value = "/admin/product")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    /**
     * 查询所有的一级分类
     * @return
     */
    @GetMapping(value = "/getCategory1")
    public Result getCategory1() {
        return Result.ok(managerService.getCategory1());
    }

    /**
     * 根据一级分类查询二级分类
     * @param cid
     * @return
     */
    @GetMapping(value = "/getCategory2/{cid}")
    public Result getCategory2(@PathVariable(value = "cid") Long cid) {
        return Result.ok(managerService.getCategory2(cid));
    }

    /**
     * 根据二级分类查询三级分类
     * @param cid
     * @return
     */
    @GetMapping(value = "/getCategory3/{cid}")
    public Result getCategory3(@PathVariable(value = "cid") Long cid) {
        return Result.ok(managerService.getCategory3(cid));
    }

    /**
     * 保存平台属性
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        return Result.ok(managerService.saveAttrInfo(baseAttrInfo));
    }

    /**
     * 根据平台分类id查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> getBaseAttrInfoByCategory(@PathVariable("category1Id") Long category1Id,
                                                                @PathVariable("category2Id") Long category2Id,
                                                                @PathVariable("category3Id") Long category3Id) {
        return Result.ok(managerService.getBaseAttrInfoByCategory(category1Id, category2Id, category3Id));
    }

    /**
     * 根据平台属性名称的id查询平台属性值的列表
     * 修改时回显平台属性值
     * @param attrId
     * @return
     */
    @GetMapping(value = "/getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> getBaseAttrValueByAttrId(@PathVariable(value = "attrId") Long attrId) {
        return Result.ok(managerService.getBaseAttrValueByAttrId(attrId));
    }

    /**
     * 查询SPU品牌列表
     * @return
     */
    @GetMapping(value = "/baseTrademark/getTrademarkList")
    public Result<List<BaseTrademark>> getBaseTrademark() {
        return Result.ok(managerService.getBaseTrademark());
    }

    /**
     * 品牌分页
     * @return
     */
    @GetMapping(value = "/baseTrademark/{page}/{size}")
    public Result pageBaseTrademark(@PathVariable(value = "page") Integer page,
                                    @PathVariable(value = "size") Integer size) {
        IPage<BaseTrademark> baseTrademarkIPage = managerService.pageBaseTrademark(page, size);
        return Result.ok(baseTrademarkIPage);
    }

    /**
     * 查询SPU所有销售属性名称列表
     * @return
     */
    @GetMapping(value = "/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> getBaseSaleAttr() {
        return Result.ok(managerService.getBaseSaleAttr());
    }

    /**
     * 保存SPU销售属性的信息/修改SPU销售属性的信息
     * @param spuInfo
     * @return
     */
    @PostMapping(value = "/saveSpuInfo")
    public Result<SpuInfo> saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        return Result.ok(managerService.saveSpuInfo(spuInfo));
    }

    /**
     * 分页查询SPU列表
     * @param page
     * @param size
     * @param category3Id
     * @return
     */
    @GetMapping(value = "/{page}/{size}")
    public Result getSpuInfoList(@PathVariable(value = "page") Integer page,
                                 @PathVariable(value = "size") Integer size,
                                 Long category3Id) {
        return Result.ok(managerService.getSpuInfoList(category3Id, page, size));
    }

    /**
     * 根据spuId查询所有销售属性和值列表
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable(value = "spuId") Long spuId) {
        return Result.ok(managerService.spuSaleAttrList(spuId));
    }

    /**
     * 根据spuId查询所有的图片列表
     * @param spuId
     * @return
     */
    @GetMapping(value = "/spuImageList/{spuId}")
    public Result spuImageList(@PathVariable(value = "spuId") Long spuId) {
        return Result.ok(managerService.spuImageList(spuId));
    }

    /**
     * 保存与修改sku信息
     * @param skuInfo
     * @return
     */
    @PostMapping(value = "/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        return Result.ok(managerService.saveSkuInfo(skuInfo));
    }

    /**
     * 分页查询skuInfo信息
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/list/{page}/{size}")
    public Result getSkuInfoList(@PathVariable("page") Integer page,
                                 @PathVariable("size") Integer size) {
        return Result.ok(managerService.getSkuInfoList(page, size));
    }

    /**
     * 商品的上架
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId) {
        return Result.ok(managerService.onSaleOrOff(skuId, ProductConst.SKU_SALE_ON));
    }

    /**
     * 商品的下架
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result offSale(@PathVariable("skuId") Long skuId) {
        return Result.ok(managerService.onSaleOrOff(skuId, ProductConst.SKU_SALE_OFF));
    }

}
