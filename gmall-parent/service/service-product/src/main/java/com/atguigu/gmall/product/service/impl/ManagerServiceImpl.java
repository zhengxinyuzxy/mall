package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.list.feign.ListFeign;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManagerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理控制台api接口服务类的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private ListFeign listFeign;

    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 根据一级分类查询二级分类
     *
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        // 参数校验
        if (category1Id == null) {
            throw new RuntimeException("参数错误！");
        }
        // 声明条件构造器
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(BaseCategory2::getCategory1Id, category1Id);
        // 执行查询,获取结果
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(wrapper);
        //返回结果
        return baseCategory2List;
    }

    /**
     * 根据二级分类查询三级分类
     *
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        // 参数校验
        if (category2Id == null) {
            throw new RuntimeException("参数错误！");
        }
        // 声明条件构造器
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        // 构建查询条件
        wrapper.eq(BaseCategory3::getCategory2Id, category2Id);
        // 执行查询,获取结果
        List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectList(wrapper);
        //返回结果
        return baseCategory3List;
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public BaseAttrInfo saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 参数校验
        if (baseAttrInfo == null) {
            throw new RuntimeException("参数错误！");
        }
        // 修改
        // 判断平台属性名称Id是否存在
        if (baseAttrInfo.getId() != null) {
            // id存在，对平台属性名称修改
            baseAttrInfoMapper.updateById(baseAttrInfo);
            // 删除平台属性值
            baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
        } else {
            // 新增平台属性
            // 保存平台属性名称
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <= 0) {
                throw new RuntimeException("保存平台属性名称错误！请重新尝试");
            }
        }
        // 获取平台属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
//        // 补全属性值-方案一
//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            // 补全平台属性值的id
//            baseAttrValue.setAttrId(baseAttrInfo.getId());
//            // 保存数据
//            baseAttrValueMapper.insert(baseAttrValue);
//        }

        // 补全属性值-方案二，数据流方式，这样所有的value都有id
        // 获取新的平台属性值集合
        List<BaseAttrValue> attrValueListNew = attrValueList.stream().map(baseAttrValue -> {
            // 补全平台属性值id
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            // 保存平台属性值
            baseAttrValueMapper.insert(baseAttrValue);
            return baseAttrValue;
        }).collect(Collectors.toList());
        // 替换旧的平台属性值集合
        baseAttrInfo.setAttrValueList(attrValueListNew);
        // 返回结果
        return baseAttrInfo;

    }

    /**
     * 根据平台一级二级三级分类id查询平台属性列表
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoByCategory(Long category1Id,
                                                        Long category2Id,
                                                        Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoByCategory(category1Id, category2Id, category3Id);
    }

    /**
     * 根据平台属性名称的id查询平台属性值的列表
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getBaseAttrValueByAttrId(Long attrId) {
        return baseAttrValueMapper.selectList(new LambdaQueryWrapper<BaseAttrValue>()
                .eq(BaseAttrValue::getAttrId, attrId));
    }

    /**
     * 查询SPU品牌列表
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getBaseTrademark() {
        return baseTrademarkMapper.selectList(null);
    }

    /**
     * 品牌分页
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseTrademark> pageBaseTrademark(int page, int size) {
        IPage<BaseTrademark> baseTrademarkIPage =
                baseTrademarkMapper.selectPage(new Page<>(page, size), null);
        return baseTrademarkIPage;
    }

    /**
     * 查询SPU销售属性列表
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttr() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 保存SPU销售属性的信息
     *
     * @param spuInfo
     */
    @Override
    public SpuInfo saveSpuInfo(SpuInfo spuInfo) {
        // 参数校验
        if (spuInfo == null) {
            throw new RuntimeException("参数错误！");
        }

        // 判断spuInfo中id是否为空，不为空说明执行修改
        if (spuInfo.getId() != null) {
            // 修改supInfo
            spuInfoMapper.updateById(spuInfo);

            // 删除旧数据
            // 1. 删除图片数据, 条件构造器判断是否等于spuId
            spuImageMapper.delete(
                    new LambdaQueryWrapper<SpuImage>()
                            .eq(SpuImage::getSpuId, spuInfo.getId()));

            // 2. 删除销售属性数据, 条件构造器判断是否等于spuId
            spuSaleAttrMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttr>().
                            eq(SpuSaleAttr::getSpuId, spuInfo.getId()));

            // 3. 删除销售属性值数据, 条件构造器判断是否等于spuId
            spuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SpuSaleAttrValue>()
                            .eq(SpuSaleAttrValue::getSpuId, spuInfo.getId()));
        } else {
            // 新增
            // 保存spu_info表的信息, 保存后存在id
            spuInfoMapper.insert(spuInfo);
        }

        // 保存sup_image表的信息, 补全spuId
        List<SpuImage> spuImages = saveSpuImageList(spuInfo.getSpuImageList(), spuInfo.getId());
        spuInfo.setSpuImageList(spuImages);

        // 保存sup_sale_attr表的信息, 补全spuId
        List<SpuSaleAttr> spuSaleAttrs =
                saveSpuSaleAttrList(spuInfo.getSpuSaleAttrList(), spuInfo.getId());
        spuInfo.setSpuSaleAttrList(spuSaleAttrs);

        // 返回spuInfo对象
        return spuInfo;
    }

    /**
     * 保存销售属性的信息
     *
     * @param spuSaleAttrList
     */
    private List<SpuSaleAttr> saveSpuSaleAttrList(List<SpuSaleAttr> spuSaleAttrList,
                                                  Long spuId) {
        // 保存sup_sale_attr_value表的信息，补全spuId, supAttrName
        return spuSaleAttrList.stream().map(spuSaleAttr -> {
            // 第一步: 补全spuSaleAttr的spuId
            spuSaleAttr.setSpuId(spuId);

            // 第二步: 保存spuSaleAttr
            spuSaleAttrMapper.insert(spuSaleAttr);

            // 第三步: 保存销售属性值的信息
            List<SpuSaleAttrValue> spuSaleAttrValueList = saveSaleAttrValue(spuSaleAttr);

            // 第四步: 替换spuSaleAttrValueList
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);

            // 第五步: 返回spuSaleAttr
            return spuSaleAttr;
        }).collect(Collectors.toList());
    }

    /**
     * 保存销售属性值的信息
     *
     * @param spuSaleAttr
     * @return
     */
    private List<SpuSaleAttrValue> saveSaleAttrValue(SpuSaleAttr spuSaleAttr) {
        // 补全, 返回新的SpuSaleAttrValueList
        return spuSaleAttr.getSpuSaleAttrValueList().stream().map(spuSaleAttrValue -> {
            // 保存spuSaleAttrValue的spuId
            spuSaleAttrValue.setSpuId(spuSaleAttr.getSpuId());

            // 保存spuSaleAttrValue的saleAttrName
            spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());

            // 保存spuSaleAttrValue
            spuSaleAttrValueMapper.insert(spuSaleAttrValue);

            // 返回spuSaleAttrValue
            return spuSaleAttrValue;
        }).collect(Collectors.toList());
    }

    /**
     * 保存spu的图片信息
     *
     * @param spuImageList
     * @param spuId
     * @return
     */
    private List<SpuImage> saveSpuImageList(List<SpuImage> spuImageList, Long spuId) {
        // 保存sup_image表的信息, 补全spuId
        List<SpuImage> spuImageListNew = spuImageList.stream().map(spuImage -> {
            // 补全spuImage的spuId
            spuImage.setSpuId(spuId);

            // 保存
            spuImageMapper.insert(spuImage);

            // 返回
            return spuImage;
        }).collect(Collectors.toList());

        // 返回spuImageList图片列表
        return spuImageListNew;
    }

    /**
     * 分页查询SPU列表信息
     *
     * @param category3Id
     * @param page
     * @param size
     */
    @Override
    public IPage<SpuInfo> getSpuInfoList(Long category3Id, Integer page, Integer size) {
        return spuInfoMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<SpuInfo>().eq(SpuInfo::getCategory3Id, category3Id)
        );
    }

    /**
     * 根据spu_sale_attr表的spuId查询所有的销售属性列表
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrBySpuId(spuId);
    }

    /**
     * 根据spuId查询SpuImage图片列表
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        return spuImageMapper.selectList(
                new LambdaQueryWrapper<SpuImage>()
                        .eq(SpuImage::getSpuId, spuId));
    }

    /**
     * 保存SKU的信息/修改SKU的信息
     *
     * @param skuInfo
     * @return
     */
    @Override
    public SkuInfo saveSkuInfo(SkuInfo skuInfo) {
        // 参数校验
        if (skuInfo == null) {
            throw new RuntimeException("参数为空！");
        }

        // 判断skuId是否为空, 不为空进行修改操作
        if (skuInfo.getId() != null) {
            // 修改
            skuInfoMapper.updateById(skuInfo);

            // 删除旧数据:
            /**
             * 1. 删除图片表数据
             * 2. 删除平台属性数据
             * 3. 删除销售属性数据
             */
            // 删除图片表数据
            skuImageMapper.delete(
                    new LambdaQueryWrapper<SkuImage>()
                            .eq(SkuImage::getSkuId, skuInfo.getId()));

            // 删除平台属性数据
            skuAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuAttrValue>()
                            .eq(SkuAttrValue::getSkuId, skuInfo.getId()));

            // 删除销售属性数据
            skuSaleAttrValueMapper.delete(
                    new LambdaQueryWrapper<SkuSaleAttrValue>()
                            .eq(SkuSaleAttrValue::getSkuId, skuInfo.getId()));
        } else {
            // 新增
            skuInfoMapper.insert(skuInfo);
        }

        // 保存sku图片表的信息
        List<SkuImage> skuImages =
                saveSkuImage(skuInfo.getSkuImageList(), skuInfo.getId());
        skuInfo.setSkuImageList(skuImages);

        // 保存sku平台属性表的信息
        List<SkuAttrValue> skuAttrValues =
                saveSkuAttrValue(skuInfo.getSkuAttrValueList(), skuInfo.getId());
        skuInfo.setSkuAttrValueList(skuAttrValues);

        // 保存sku销售属性表的信息
        List<SkuSaleAttrValue> skuSaleAttrValues =
                saveSkuSaleAttrValue(
                        skuInfo.getSkuSaleAttrValueList(),
                        skuInfo.getId(),
                        skuInfo.getSpuId());
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValues);

        // 返回结果
        return skuInfo;
    }

    /**
     * 保存sku销售属性表的信息
     *
     * @param skuSaleAttrValueList
     */
    private List<SkuSaleAttrValue> saveSkuSaleAttrValue(List<SkuSaleAttrValue> skuSaleAttrValueList, Long skuId, Long spuId) {
        // 补全与保存
        return skuSaleAttrValueList.stream().map(skuSaleAttrValue -> {
            // 补全skuId
            skuSaleAttrValue.setSkuId(skuId);

            // 补全spuId
            skuSaleAttrValue.setSpuId(spuId);

            // 保存skuSaleAttrValue
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);

            // 返回skuSaleAttrValue
            return skuSaleAttrValue;
        }).collect(Collectors.toList());
    }

    /**
     * 保存sku平台属性表的信息
     *
     * @param skuAttrValueList
     */
    private List<SkuAttrValue> saveSkuAttrValue(List<SkuAttrValue> skuAttrValueList, Long skuId) {
        // 补全与保存
        return skuAttrValueList.stream().map(skuAttrValue -> {
            // 补全skuId
            skuAttrValue.setSkuId(skuId);

            // 保存skuAttrValue
            skuAttrValueMapper.insert(skuAttrValue);

            // 返回skuAtrValue
            return skuAttrValue;
        }).collect(Collectors.toList());

    }

    /**
     * 保存sku图片表的信息
     *
     * @param skuImageList
     */
    private List<SkuImage> saveSkuImage(List<SkuImage> skuImageList, Long skuId) {
        // 补全与保存
        return skuImageList.stream().map(skuImage -> {
            // 补全skuId
            skuImage.setSkuId(skuId);

            //保存skuImage
            skuImageMapper.insert(skuImage);

            // 返回skuImage
            return skuImage;
        }).collect(Collectors.toList());
    }

    /**
     * 分页查询skuInfo信息
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> getSkuInfoList(Integer page, Integer size) {
        return skuInfoMapper.selectPage(
                new Page<SkuInfo>(page, size),
                new LambdaQueryWrapper<SkuInfo>()
                        .orderByAsc(SkuInfo::getSpuId)
                        .orderByDesc(SkuInfo::getId));
    }

    /**
     * 商品的上架和下架
     *
     * @param skuId
     * @param status
     * @return
     */
    @Override
    public SkuInfo onOroOffSale(Long skuId, Short status) {
        // 参数校验
        if (skuId == null) {
            throw new RuntimeException("参数异常!");
        }
        // 获取skuInfo的信息
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo == null || skuInfo.getId() == null) {
            throw new RuntimeException("参数异常！");
        }
        if (status.intValue() == skuInfo.getIsSale()) {
            throw new RuntimeException("商品上下架状态一样，不能修改！请检查");
        }
        //修改skuinfo中关于上下架的属性值
        skuInfo.setIsSale(status.intValue());
        // 重新设置回skuInfo的信息
        skuInfoMapper.updateById(skuInfo);
        // 判断是上架还是下架,feign调用不同的上下架方法
        if (status.equals(ProductConst.SKU_SALE_ON)) {
            // 上架
            listFeign.upper(skuId);
        } else {
            // 下架
            listFeign.down(skuId);
        }
        // feign调用service-list的上下架方法
        return skuInfo;
    }

}
