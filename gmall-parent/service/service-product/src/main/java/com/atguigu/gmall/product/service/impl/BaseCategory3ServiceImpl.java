package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.BaseCategory3Service;
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
public class BaseCategory3ServiceImpl
        extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
        implements BaseCategory3Service {

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    /**
     * 条件查询
     * @param baseCategory3
     * @return
     */
    @Override
    public List<BaseCategory3> search(BaseCategory3 baseCategory3) {
        // 参数校验
        if (baseCategory3 == null) {
            // 若没有条件，执行查询所有数据返回
            return baseCategory3Mapper.selectList(null);
        }
//        // 声明条件构造器
//        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
//        // 拼接条件
//        // 判断id是否为空
//        if (baseCategory3.getId() != null) {
//            wrapper.eq(BaseCategory3::getId, baseCategory3.getId());
//        }
//        // 判断name是否为空
//        if (!StringUtils.isEmpty(baseCategory3.getName())) {
//            wrapper.like(BaseCategory3::getName, baseCategory3.getName());
//        }
        // 拼接条件
        LambdaQueryWrapper wrapper = getQueryWrapper(baseCategory3);
        // 执行条件查询获取结果
        List<BaseCategory3> list = baseCategory3Mapper.selectList(wrapper);
        // 返回结果
        return list;
    }

    /**
     * 分页查询
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return
     */
    @Override
    public IPage<BaseCategory3> pageSearch(Integer page, Integer size) {
        // 条件校验
        if (page == null || size == null) {
            throw new RuntimeException("参数错误！");
        }
        // 构建分页信息，执行查询获取结果
        IPage<BaseCategory3> baseCategory3IPage = baseCategory3Mapper.selectPage(new Page<>(page, size), null);
        // 返回结果
        return baseCategory3IPage;
    }

    /**
     * 分页条件查询
     * @param baseCategory3
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseCategory3> search(BaseCategory3 baseCategory3, Integer page, Integer size) {
        // 参数校验
        if (baseCategory3 == null || page == null || size == null) {
            throw new RuntimeException("参数错误！");
        }
        // 拼接条件
        LambdaQueryWrapper wrapper = getQueryWrapper(baseCategory3);

        // 声明分页条件信息，执行分页条件查询获取返回结果
        IPage<BaseCategory3> baseCategory3IPage = baseCategory3Mapper.selectPage(new Page<>(page, size), wrapper);

        // 返回结果
        return baseCategory3IPage;
    }

    /**
     * 提取声明构造器
     * @return
     */
    private LambdaQueryWrapper getQueryWrapper(BaseCategory3 baseCategory3) {
        // 声明条件构造器
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        // 拼接条件
        // 判断id是否为空
        if (baseCategory3.getId() != null) {
            wrapper.eq(BaseCategory3::getId, baseCategory3.getId());
        }
        // 判断name是否为空
        if (!StringUtils.isEmpty(baseCategory3.getName())) {
            wrapper.like(BaseCategory3::getName, baseCategory3.getName());
        }
        return wrapper;
    }

}
