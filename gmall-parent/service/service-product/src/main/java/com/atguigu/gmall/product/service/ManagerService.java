package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 管理控制台使用的api接口的service
 */
public interface ManagerService {
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    public List<BaseCategory1> getCategory1();

    /**
     * 根据一级分类查询二级分类
     *
     * @param category1Id
     * @return
     */
    public List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 根据二级分类查询三级分类
     *
     * @param category2Id
     * @return
     */
    public List<BaseCategory3> getCategory3(Long category2Id);

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    public BaseAttrInfo saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台分类id查询平台属性列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> getBaseAttrInfoByCategory(Long category1Id,
                                                        Long category2Id,
                                                        Long category3Id);

    /**
     * 根据平台属性名称的id查询平台属性值的列表
     *
     * @param attrId
     * @return
     */
    public List<BaseAttrValue> getBaseAttrValueByAttrId(Long attrId);

    /**
     * 查询SPU品牌列表
     *
     * @return
     */
    public List<BaseTrademark> getBaseTrademark();

    /**
     * 品牌分页
     *
     * @return
     */
    public IPage<BaseTrademark> pageBaseTrademark(int page, int size);

    /**
     * 查询SPU销售属性列表
     *
     * @return
     */
    public List<BaseSaleAttr> getBaseSaleAttr();

    /**
     * 保存SPU的信息
     */
    public SpuInfo saveSpuInfo(SpuInfo spuInfo);

    /**
     * 分页查询SPU列表信息
     *
     * @param category3Id
     * @param page
     * @param size
     */
    public IPage<SpuInfo> getSpuInfoList(Long category3Id, Integer page, Integer size);

    /**
     * 根据spu_sale_attr表的spuId查询所有的销售属性列表
     *
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId);

    /**
     * 根据spuId查询SpuImage图片列表
     *
     * @param spuId
     * @return
     */
    public List<SpuImage> spuImageList(Long spuId);

    /**
     * 保存SKU的信息/修改SKU的信息
     *
     * @param skuInfo
     * @return
     */
    public SkuInfo saveSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询skuInfo信息
     *
     * @param page
     * @param size
     * @return
     */
    public IPage<SkuInfo> getSkuInfoList(Integer page, Integer size);

    /**
     * 商品的上架和下架
     *
     * @param skuId
     * @param status
     * @return
     */
    public SkuInfo onOroOffSale(Long skuId, Short status);

}
