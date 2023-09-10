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

    /**
     * 首页三级分类
     * @return
     */
    @GmallCache(prefix = "getIndexCategory:")
    @GetMapping("/getIndexCategory")
    public Result getIndexCategory() {
        return Result.ok(indexService.getIndexCategory());
    }

}
