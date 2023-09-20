package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAddressMapper useraddressmapper;

    /**
     * 根据用户的登录名查询用户的基本信息，隐藏在token中
     * @param username
     * @return
     */
    @Override
    public UserInfo getUserInfoByLoginName(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getLoginName, username));
    }

    /**
     * 获取用户的收获地址列表
     * @param username
     */
    @Override
    public List<UserAddress> getUserAddresses(String username) {
        List<UserAddress> userAddressList = useraddressmapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, username));
        return userAddressList;
    }
}
