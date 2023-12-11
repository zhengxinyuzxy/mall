package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartInfoController {

    @Autowired
    private CartService cartService;

    /**
     * 新增购物车
     * @param num
     * @param skuId
     * @return
     */
    @GetMapping("/addCart")
    public Result addCart(Integer num, Long skuId) {
        cartService.addCartInfo(skuId, num);
        return Result.ok();
    }

    /**
     * 查询购物车数据
     * @return
     */
    @GetMapping("/getCartInfo")
    public Result getCartInfo() {
        List<CartInfo> cartInfoList = cartService.getCartInfo();
        return Result.ok(cartInfoList);
    }

    /**
     * 删除购物车数据
     * @param id
     * @return
     */
    @GetMapping("/deleteCartInfo")
    public Result deleteCartInfo(Long id) {
        cartService.del(id);
        return Result.ok();
    }

    /**
     * 修改购物车的选中状态
     * @param id
     * @param status
     * @return
     */
    public Result changeChecked(Long id, Integer status) {
        cartService.changeChecked(id, status);
        return Result.ok();
    }

    /**
     * 合并购物车
     * @param cartInfoList
     * @return
     */
    @PostMapping("/mergeCartInfo")
    public Result mergeCartInfo(@RequestBody List<CartInfo> cartInfoList) {
        cartService.mergeCartInfo(cartInfoList);
        return Result.ok();
    }

    /**
     * 查询订单确认页面的购物车详情和总金额总数量
     * @return
     */
    @GetMapping(value = "/getOrderConfirm")
    public Result getOrderConfirm() {
        return Result.ok(cartService.getOrderConfirm());
    }

    /**
     * 下单使用的查询接口
     * @return
     */
    @GetMapping(value = "/getAddOrderInfo")
    public Map<String, Object> getAddOrderInfo() {
        return cartService.getOrderConfirm();
    }

}
