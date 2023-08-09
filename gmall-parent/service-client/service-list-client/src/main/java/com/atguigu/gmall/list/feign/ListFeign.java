package com.atguigu.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "service-list", path = "/api/list")
public interface ListFeign {

    /**
     * 商品的上架
     */
    @GetMapping("/upper/{skuId}")
    public void upper(@PathVariable("skuId") Long skuId);


    /**
     * 商品的下架
     */
    @GetMapping("/down/{skuId}")
    public void down(@PathVariable("skuId") Long skuId);

    /**
     * 商品的搜索
     * @param SearchMap
     * @return
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam Map<String, String> SearchMap);
}
