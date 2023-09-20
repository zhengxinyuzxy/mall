package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

public interface LoginService {

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public AuthToken login(String username, String password);

    /**
     * 测试-TODO
     * @param username
     * @param password
     * @return
     */
    public AuthToken loginTest(String username, String password);
}
