package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu_sale_attr销售属性名表的mapper映射
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 根据spuId查询SpuSaleAttr所有的属性和值列表
     * @param spuId
     * @return
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuId(@Param("spuId") Long spuId);

    /**
     * 根据spuId, skuId查询销售属性和属性值, 标识唯一的选中商品
     * @param spuId
     * @param skuId
     * @return
     */
    public List<SpuSaleAttr> selectSpuSaleAttrBySpuIdBySkuId(@Param("spuId") Long spuId,
                                                             @Param("skuId") Long skuId);
}
