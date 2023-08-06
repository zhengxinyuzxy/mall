package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * sku_sale_attr_value表的mapper映射
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 根据spu的id查询sku的销售属性键值对
     *
     * @param spuId
     * @return
     */
    @Select("SELECT\n" +
            "\tsku_id,\n" +
            "\tGROUP_CONCAT( DISTINCT sale_attr_value_id ORDER BY sale_attr_value_id SEPARATOR ',' ) AS skuSaleAttrValuesId \n" +
            "FROM\n" +
            "\tsku_sale_attr_value \n" +
            "WHERE\n" +
            "\tspu_id = #{spuId} \n" +
            "GROUP BY\n" +
            "\tsku_id")
    public List<Map> selectSkuSaleAttrValueKeys(Long spuId);
}
