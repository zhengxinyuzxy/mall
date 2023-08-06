package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * 一级分类接口类的实现类
 */
@Service
public class BaseCategory2ServiceImpl
        extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
        implements BaseCategory2Service {

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    /**
     * 条件查询
     *
     * @param baseCategory2
     * @return
     */
    @Override
    public List<BaseCategory2> search(BaseCategory2 baseCategory2) {
        // 参数校验
        if (baseCategory2 == null) {
            // 若没有条件，执行查询所有数据返回
            return baseCategory2Mapper.selectList(null);
        }
//        // 声明条件构造器
//        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
//        // 拼接条件
//        // 判断id是否为空
//        if (baseCategory2.getId() != null) {
//            wrapper.eq(BaseCategory2::getId, baseCategory2.getId());
//        }
//        // 判断name是否为空
//        if (!StringUtils.isEmpty(baseCategory2.getName())) {
//            wrapper.like(BaseCategory2::getName, baseCategory2.getName());
//        }
        // 拼接条件
        LambdaQueryWrapper wrapper = getQueryWrapper(baseCategory2);
        // 执行条件查询获取结果
        List<BaseCategory2> list = baseCategory2Mapper.selectList(wrapper);
        // 返回结果
        return list;
    }

    /**
     * 分页查询
     *
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return
     */
    @Override
    public IPage<BaseCategory2> pageSearch(Integer page, Integer size) {
        // 条件校验
        if (page == null || size == null) {
            throw new RuntimeException("参数错误！");
        }
        // 构建分页信息，执行查询获取结果
        IPage<BaseCategory2> baseCategory2IPage = baseCategory2Mapper.selectPage(new Page<>(page, size), null);
        // 返回结果
        return baseCategory2IPage;
    }

    /**
     * 分页条件查询
     *
     * @param baseCategory2
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseCategory2> search(BaseCategory2 baseCategory2, Integer page, Integer size) {
        // 参数校验
        if (baseCategory2 == null || page == null || size == null) {
            throw new RuntimeException("参数错误！");
        }
        // 拼接条件
        LambdaQueryWrapper wrapper = getQueryWrapper(baseCategory2);

        // 声明分页条件信息，执行分页条件查询获取返回结果
        IPage<BaseCategory2> baseCategory2IPage = baseCategory2Mapper.selectPage(new Page<>(page, size), wrapper);

        // 返回结果
        return baseCategory2IPage;
    }

    /**
     * 提取声明构造器
     *
     * @return
     */
    private LambdaQueryWrapper getQueryWrapper(BaseCategory2 baseCategory2) {
        // 声明条件构造器
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        // 拼接条件
        // 判断id是否为空
        if (baseCategory2.getId() != null) {
            wrapper.eq(BaseCategory2::getId, baseCategory2.getId());
        }
        // 判断name是否为空
        if (!StringUtils.isEmpty(baseCategory2.getName())) {
            wrapper.like(BaseCategory2::getName, baseCategory2.getName());
        }
        return wrapper;
    }

}
