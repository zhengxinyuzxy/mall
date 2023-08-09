package com.atguigu.gmall.item.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "service-item", path = "/client/item")
public interface ItemFeign {
    /**
     * 商品详情的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuItem/{skuId}")
    public Map<String, Object> getSkuItem(@PathVariable("skuId") Long skuId);
}
