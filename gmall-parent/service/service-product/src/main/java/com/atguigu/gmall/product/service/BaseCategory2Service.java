package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 一级分类信息的service接口类
 */
public interface BaseCategory2Service extends IService<BaseCategory2> {

    /**
     * 条件查询
     * @param baseCategory2
     * @return
     */
    public List<BaseCategory2> search(BaseCategory2 baseCategory2);

    /**
     * 分页查询
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return
     */
    public IPage<BaseCategory2> pageSearch(Integer page, Integer size);

    /**
     * 分页条件查询
     * @param baseCategory2
     * @return
     */
    public IPage<BaseCategory2> search(BaseCategory2 baseCategory2, Integer page, Integer size);
}
