package com.atguigu.gmall.oauth.intercepter;

import com.atguigu.gmall.oauth.util.AdminJwtUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GmallAuthRequestInterceptor implements RequestInterceptor {
    /**
     * feign发起远程调用前拦截请求，在请求头中添加临时令牌，访问用户信息，相当于增强方法
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = AdminJwtUtil.adminJwt();
        requestTemplate.header("Authorization", "bearer" + token);
    }
}
