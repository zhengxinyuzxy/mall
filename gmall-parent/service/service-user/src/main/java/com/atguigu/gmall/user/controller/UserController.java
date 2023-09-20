package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import com.atguigu.gmall.user.util.GmallThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/info")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户的基本信息
     * @param username
     * @return
     */
    @GetMapping("/getUserInfo/{username}")
    public UserInfo getUserInfo(@PathVariable("username") String username) {
        return userService.getUserInfoByLoginName(username);
    }

    /**
     * 获取用户的收货地址列表
     * @return
     */
    @GetMapping("/getUserAddress")
    public Result getUserAddresses() {
        String userName = GmallThreadLocalUtils.getUserName();
        return Result.ok(userService.getUserAddresses(userName));
    }

}
