package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.item.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 商品详情微服务的外部controller
 */
@RestController
@RequestMapping(value = "/client/item")
//@Api(value = "Item-Controller", tags = "商品详情微服务的外部controller")
public class ItemController {

    //    @Autowired
    @Resource
    private ItemService itemService;

    /**
     * 商品详情的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getSkuItem/{skuId}")
    public Map<String, Object> getSkuItem(@PathVariable("skuId") Long skuId) {
        return itemService.getSkuItem(skuId);
    }

}
