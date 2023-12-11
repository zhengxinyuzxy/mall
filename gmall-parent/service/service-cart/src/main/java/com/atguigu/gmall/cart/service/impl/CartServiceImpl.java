package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.util.GmallThreadLocalUtils;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增购物车
     * @param skuId
     * @param num
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCartInfo(Long skuId, Integer num) {
        // 参数校验
        if (skuId == null || num == null) {
            return;
        }
        String userName = GmallThreadLocalUtils.getUserName();
        // 查询购物车数据
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, userName)
                .eq(CartInfo::getSkuId, skuId));
        // 当这个商品已经存在购物车中
        if (cartInfo != null && cartInfo.getId() != null) {
            // 修改购物车的数据
            num = cartInfo.getSkuNum() + num;
            if (num <= 0) {
                // 删除购物车的数据
                cartInfoMapper.deleteById(cartInfo.getId());
                return;
            } else {
                cartInfo.setSkuNum(num);
                cartInfoMapper.updateById(cartInfo);
                return;
            }
        }
        // 新增购物车
        if (num <= 0) {
            return;
        }
        // 查询商品信息
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if (skuInfo == null || skuInfo.getId() == null) {
            return;
        }
        // 补全cartInfo对象的信息
        cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userName);
        // 查询sku的价格
        BigDecimal price = productFeign.getSkuInfoPrice(skuId);
        cartInfo.setCartPrice(price);
        cartInfo.setSkuNum(num);
        cartInfo.setIsChecked(1);
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        // 保存购物车信息
        cartInfoMapper.insert(cartInfo);
    }

    /**
     * 查询购物车数据
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo() {
        String userName = GmallThreadLocalUtils.getUserName();
        // 先从redis中获取数据
        List<CartInfo> cartInfoList = (List<CartInfo>) redisTemplate.opsForValue().get(userName);
        // 判断
        if (cartInfoList == null || cartInfoList.isEmpty()) {
            cartInfoList = cartInfoMapper.selectList(
                    new LambdaQueryWrapper<CartInfo>()
                            .eq(CartInfo::getUserId, userName));
            // 将数据存入redis
            redisTemplate.opsForValue().set(userName, cartInfoList);
        }
        // redis有数据返回
        return cartInfoList;
    }

    /**
     * 删除购物车
     * @param id
     * @return
     */
    @Override
    public List<CartInfo> del(Long id) {
        String userName = GmallThreadLocalUtils.getUserName();
        // 删除redis数据
        redisTemplate.delete(userName);
        // 删除数据库数据
        cartInfoMapper.deleteById(id);
        return getCartInfo();
    }

    /**
     * 修改选中状态
     * @param id
     * @param status
     */
    @Override
    public void changeChecked(Long id, Integer status) {
        String userName = GmallThreadLocalUtils.getUserName();
        // 删除redis数据
        redisTemplate.delete(userName);
        if (id != null) {
            // 修改一条数据
            CartInfo cartInfo = cartInfoMapper.selectById(id);
            if (cartInfo == null || cartInfo.getId() == null) {
                return;
            }
            // 修改选中的状态
            cartInfo.setIsChecked(status);
            // 修改
            cartInfoMapper.updateById(cartInfo);
        }
        // 全选或者全部取消
        cartInfoMapper.updateCartInfo(status, userName);
    }

    /**
     * 合并购物车
     * @param cartInfos
     */
    @Override
    public void mergeCartInfo(List<CartInfo> cartInfos) {
        for (CartInfo cartInfo : cartInfos) {
            // 新增购物车
            addCartInfo(cartInfo.getSkuId(), cartInfo.getSkuNum());
        }
    }

    @Override
    public Map<String, Object> getOrderConfirm() {
        Map<String, Object> result = new HashMap<>();
        // 获取用户名称
        String userName = GmallThreadLocalUtils.getUserName();
        // 查询用户购物车选中商品
        List<CartInfo> cartInfoList = cartInfoMapper.selectList(
                new LambdaQueryWrapper<CartInfo>()
                        .eq(CartInfo::getUserId, userName)
                        .eq(CartInfo::getIsChecked, 1));
        // 判断是否为空
        if (cartInfoList == null || cartInfoList.isEmpty()) {
            return null;
        }
        // TODO
        result.put("cartInfoList", cartInfoList);
        // 计算金额和数量
        AtomicInteger totolNum = new AtomicInteger(0);
        AtomicDouble totolPrice = new AtomicDouble(0);
        List<CartInfo> collect = cartInfoList.stream().map(cartInfo -> {
            Integer skuNum = cartInfo.getSkuNum();
            totolNum.addAndGet(skuNum);
            BigDecimal skuInfoPrice = productFeign.getSkuInfoPrice(cartInfo.getSkuId());
            totolPrice.addAndGet(skuNum * skuInfoPrice.doubleValue());
            return cartInfo;
        }).collect(Collectors.toList());
        // 包装返回数据
        result.put("totolNum", totolNum);
        result.put("totolPrice", totolPrice);
        result.put("cartInfoList", JSONObject.toJSONString(collect));
        // 返回数据
        return result;
    }

}
