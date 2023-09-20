package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 一级分类信息的service接口类
 */
public interface BaseCategory3Service extends IService<BaseCategory3> {

    /**
     * 条件查询
     * @param baseCategory3
     * @return
     */
    public List<BaseCategory3> search(BaseCategory3 baseCategory3);

    /**
     * 分页查询
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return
     */
    public IPage<BaseCategory3> pageSearch(Integer page, Integer size);

    /**
     * 分页条件查询
     * @param baseCategory3
     * @return
     */
    public IPage<BaseCategory3> search(BaseCategory3 baseCategory3, Integer page, Integer size);
}
