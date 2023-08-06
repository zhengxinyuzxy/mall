package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GmallCache(prefix = "getBaseCategoryView:")
    @GetMapping("/getBaseCategoryView")
    public Result getBaseCategoryView() {
        return Result.ok(indexService.getIndexCategory());
    }
}
