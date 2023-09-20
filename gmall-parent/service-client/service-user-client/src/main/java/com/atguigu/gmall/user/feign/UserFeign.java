package com.atguigu.gmall.user.feign;

import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "service-user", path = "/api/user/info")
public interface UserFeign {
    /**
     * 获取用户的基本信息
     * @param username
     * @return
     */
    @GetMapping("/getUserInfo/{username}")
    public UserInfo getUserInfo(@PathVariable("username") String username);

}
