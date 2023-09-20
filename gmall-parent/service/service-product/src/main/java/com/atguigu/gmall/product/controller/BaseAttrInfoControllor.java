package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
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
@RequestMapping(value = "/baseAttrInfo")
public class BaseAttrInfoControllor {

    // 装配服务接口
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据id查找
     * @param id
     * @return
     */
    @GetMapping(value = "/findById/{id}")
    public Result<BaseAttrInfo> findById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseAttrInfoService.getById(id));
    }

    /**
     * 查找所有
     * @return
     */
    @GetMapping(value = "/findAll")
    public Result<List<BaseAttrInfo>> findAll() {
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoService.list(null);
        return Result.ok(baseAttrInfoList);
    }

    // RestFul风格对于新增，修改，删除可以省略匹配映射

    /**
     * 新增
     * @param baseAttrInfo
     * @return
     */
    @PostMapping
    public Result add(@RequestBody BaseAttrInfo baseAttrInfo) {
        return Result.ok(baseAttrInfoService.save(baseAttrInfo));
    }

    /**
     * 修改
     * @param baseAttrInfo
     * @return
     */
    @PutMapping
    public Result update(@RequestBody BaseAttrInfo baseAttrInfo) {
        return Result.ok(baseAttrInfoService.updateById(baseAttrInfo));
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result removeById(@PathVariable(value = "id") Long id) {
        return Result.ok(baseAttrInfoService.removeById(id));
    }

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    @PostMapping(value = "/search")
    public Result search(@RequestBody BaseAttrInfo baseAttrInfo) {
        return Result.ok(baseAttrInfoService.search(baseAttrInfo));
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
        return Result.ok(baseAttrInfoService.pageSearch(page, size));
    }

    /**
     * 分页条件查询
     * @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/pageConditionSearch/{page}/{size}")
    public Result pageConditionSearch(@RequestBody BaseAttrInfo baseAttrInfo,
                                      @PathVariable(value = "page") Integer page,
                                      @PathVariable(value = "size") Integer size) {
        return Result.ok(baseAttrInfoService.search(baseAttrInfo, page, size));
    }
}
