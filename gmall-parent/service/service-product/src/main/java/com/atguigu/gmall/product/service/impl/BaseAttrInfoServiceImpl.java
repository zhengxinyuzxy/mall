package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 一级分类接口类的实现类
 */
@Service
public class BaseAttrInfoServiceImpl
        extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 条件查询
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo) {
        // 参数校验
        if (baseAttrInfo == null) {
            // 若没有条件，执行查询所有数据返回
            return baseAttrInfoMapper.selectList(null);
        }
//        // 声明条件构造器
//        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
//        // 拼接条件
//        // 判断id是否为空
//        if (baseAttrInfo.getId() != null) {
//            wrapper.eq(BaseAttrInfo::getId, baseAttrInfo.getId());
//        }
//        // 判断name是否为空
//        if (!StringUtils.isEmpty(baseAttrInfo.getName())) {
//            wrapper.like(BaseAttrInfo::getName, baseAttrInfo.getName());
//        }
        // 拼接条件
        LambdaQueryWrapper wrapper = getQueryWrapper(baseAttrInfo);
        // 执行条件查询获取结果
        List<BaseAttrInfo> list = baseAttrInfoMapper.selectList(wrapper);
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
    public IPage<BaseAttrInfo> pageSearch(Integer page, Integer size) {
        // 条件校验
        if (page == null || size == null) {
            throw new RuntimeException("参数错误！");
        }
        // 构建分页信息，执行查询获取结果
        IPage<BaseAttrInfo> baseAttrInfoIPage = baseAttrInfoMapper.selectPage(new Page<>(page, size), null);
        // 返回结果
        return baseAttrInfoIPage;
    }

    /**
     * 分页条件查询
     *
     * @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> search(BaseAttrInfo baseAttrInfo, Integer page, Integer size) {
        // 参数校验
        if (baseAttrInfo == null || page == null || size == null) {
            throw new RuntimeException("参数错误！");
        }
        // 拼接条件
        LambdaQueryWrapper wrapper = getQueryWrapper(baseAttrInfo);

        // 声明分页条件信息，执行分页条件查询获取返回结果
        IPage<BaseAttrInfo> baseAttrInfoIPage = baseAttrInfoMapper.selectPage(new Page<>(page, size), wrapper);

        // 返回结果
        return baseAttrInfoIPage;
    }

    /**
     * 提取声明构造器
     *
     * @return
     */
    private LambdaQueryWrapper getQueryWrapper(BaseAttrInfo baseAttrInfo) {
        // 声明条件构造器
        LambdaQueryWrapper<BaseAttrInfo> wrapper = new LambdaQueryWrapper<>();
        // 拼接条件
        // 判断id是否为空
        if (baseAttrInfo.getId() != null && baseAttrInfo.getId() != 0) {
            wrapper.eq(BaseAttrInfo::getId, baseAttrInfo.getId());
        }
        // 判断name是否为空
        if (StringUtils.isNotBlank(baseAttrInfo.getAttrName())) {
            wrapper.like(BaseAttrInfo::getAttrName, baseAttrInfo.getAttrName());
        }
        return wrapper;
    }

}
