package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
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
@RequestMapping(value = "/category1")
public class BaseCategory1Controllor {

    // 装配服务接口
    @Autowired
    private BaseCategory1Service baseCategory1Service;

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/findById/{id}")
    public Result<BaseCategory1> findById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseCategory1Service.getById(id));
    }

    /**
     * 查找所有
     *
     * @return
     */
    @GetMapping(value = "/findAll")
    public Result<List<BaseCategory1>> findAll() {
        List<BaseCategory1> baseCategory1List = baseCategory1Service.list(null);
        return Result.ok(baseCategory1List);
    }

    // RestFul风格对于新增，修改，删除可以省略匹配映射

    /**
     * 新增
     *
     * @param baseCategory1
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseCategory1 baseCategory1) {
        return Result.ok(baseCategory1Service.save(baseCategory1));
    }

    /**
     * 修改
     *
     * @param baseCategory1
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseCategory1 baseCategory1) {
        return Result.ok(baseCategory1Service.updateById(baseCategory1));
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result removeById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseCategory1Service.removeById(id));
    }

    /**
     * 条件查询
     *
     * @param baseCategory1
     * @return
     */
    @PostMapping(value = "/search")
    public Result search(@RequestBody BaseCategory1 baseCategory1) {
        return Result.ok(baseCategory1Service.search(baseCategory1));
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
        return Result.ok(baseCategory1Service.pageSearch(page, size));
    }

    /**
     * 分页条件查询
     *
     * @param baseCategory1
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/pageConditionSearch/{page}/{size}")
    public Result pageConditionSearch(@RequestBody BaseCategory1 baseCategory1,
                                      @PathVariable(value = "page") Integer page,
                                      @PathVariable(value = "size") Integer size) {
        return Result.ok(baseCategory1Service.search(baseCategory1, page, size));
    }
}
