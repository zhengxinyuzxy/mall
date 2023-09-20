package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台属性名称的mapper映射
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * 根据平台分类id查询平台属性列表
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoByCategory(@Param("category1Id") Long category1Id,
                                                           @Param("category2Id") Long category2Id,
                                                           @Param("category3Id") Long category3Id);

    /**
     * 根据skuId查询平台属性信息
     * @param skuId
     * @return
     */
    public List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@Param("skuId") Long skuId);
}
