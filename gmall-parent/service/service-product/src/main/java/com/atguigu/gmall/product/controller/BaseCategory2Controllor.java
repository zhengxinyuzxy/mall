package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 一级分类的controller层
 * - id查询
 * - 查询全部
 * - 新增
 * - 修改
 * - 删除
 * - 条件查询
 * - 分页查询
 * - 分页条件查询
 */
@RestController
@RequestMapping(value = "/category2")
public class BaseCategory2Controllor {

    // 装配服务接口
    @Autowired
    private BaseCategory2Service baseCategory2Service;

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/findById/{id}")
    public Result<BaseCategory2> findById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseCategory2Service.getById(id));
    }

    /**
     * 查找所有
     *
     * @return
     */
    @GetMapping(value = "/findAll")
    public Result<List<BaseCategory2>> findAll() {
        List<BaseCategory2> baseCategory2List = baseCategory2Service.list(null);
        return Result.ok(baseCategory2List);
    }

    // RestFul风格对于新增，修改，删除可以省略匹配映射

    /**
     * 新增
     *
     * @param baseCategory2
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseCategory2 baseCategory2) {
        return Result.ok(baseCategory2Service.save(baseCategory2));
    }

    /**
     * 修改
     *
     * @param baseCategory2
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseCategory2 baseCategory2) {
        return Result.ok(baseCategory2Service.updateById(baseCategory2));
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result removeById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseCategory2Service.removeById(id));
    }

    /**
     * 条件查询
     *
     * @param baseCategory2
     * @return
     */
    @PostMapping(value = "/search")
    public Result search(@RequestBody BaseCategory2 baseCategory2) {
        return Result.ok(baseCategory2Service.search(baseCategory2));
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/pageSearch/{page}/{size}")
    public Result getPage(@PathVariable(value = "page") Integer page,
                          @PathVariable(value = "size") Integer size) {
        return Result.ok(baseCategory2Service.pageSearch(page, size));
    }

    /**
     * 分页条件查询
     *
     * @param baseCategory2
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/pageConditionSearch/{page}/{size}")
    public Result pageConditionSearch(@RequestBody BaseCategory2 baseCategory2,
                                      @PathVariable(value = "page") Integer page,
                                      @PathVariable(value = "size") Integer size) {
        return Result.ok(baseCategory2Service.search(baseCategory2, page, size));
    }
}
