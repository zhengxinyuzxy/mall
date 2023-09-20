package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 购物车表的mapper映射
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {

    @Update("updata cart_info set is_checked=#{status} where user_id = #{username}")
    public int updateCartInfo(@Param("status") Integer status,
                              @Param("username") String username);
}
