package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory3Service;
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
@RequestMapping(value = "/category3")
public class BaseCategory3Controllor {

    // 装配服务接口
    @Autowired
    private BaseCategory3Service baseCategory3Service;

    /**
     * 根据id查找
     * @param id
     * @return
     */
    @GetMapping(value = "/findById/{id}")
    public Result<BaseCategory3> findById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseCategory3Service.getById(id));
    }

    /**
     * 查找所有
     * @return
     */
    @GetMapping(value = "/findAll")
    public Result<List<BaseCategory3>> findAll() {
        List<BaseCategory3> baseCategory3List = baseCategory3Service.list(null);
        return Result.ok(baseCategory3List);
    }

    // RestFul风格对于新增，修改，删除可以省略匹配映射

    /**
     * 新增
     * @param baseCategory3
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseCategory3 baseCategory3) {
        return Result.ok(baseCategory3Service.save(baseCategory3));
    }

    /**
     * 修改
     * @param baseCategory3
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseCategory3 baseCategory3) {
        return Result.ok(baseCategory3Service.updateById(baseCategory3));
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result removeById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseCategory3Service.removeById(id));
    }

    /**
     * 条件查询
     * @param baseCategory3
     * @return
     */
    @PostMapping(value = "/search")
    public Result search(@RequestBody BaseCategory3 baseCategory3) {
        return Result.ok(baseCategory3Service.search(baseCategory3));
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/pageSearch/{page}/{size}")
    public Result getPage(@PathVariable(value = "page") Integer page,
                          @PathVariable(value = "size") Integer size) {
        return Result.ok(baseCategory3Service.pageSearch(page, size));
    }

    /**
     * 分页条件查询
     * @param baseCategory3
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/pageConditionSearch/{page}/{size}")
    public Result pageConditionSearch(@RequestBody BaseCategory3 baseCategory3,
                                      @PathVariable(value = "page") Integer page,
                                      @PathVariable(value = "size") Integer size) {
        return Result.ok(baseCategory3Service.search(baseCategory3, page, size));
    }
}
