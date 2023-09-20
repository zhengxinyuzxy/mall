package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 一级分类信息的service接口类
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo);

    /**
     * 分页查询
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return
     */
    public IPage<BaseAttrInfo> pageSearch(Integer page, Integer size);

    /**
     * 分页条件查询
     * @param baseAttrInfo
     * @return
     */
    public IPage<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo, Integer page, Integer size);
}
