package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.gateway.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关的全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 过滤器的自定义逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request
        ServerHttpRequest request = exchange.getRequest();
        // 获取response
        ServerHttpResponse response = exchange.getResponse();
        // 获取token，从url获取
        String token = request.getQueryParams().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            // 如果url中没有从请求头中获取token
            token = request.getHeaders().getFirst("token");
            // 请求头没有，从cookie中获取
            HttpCookie cookie = request.getCookies().getFirst("token");
            if (cookie != null) {
                token = cookie.getValue();
            }
        }
        // 判断token是否为空
        if (StringUtils.isEmpty(token)) {
            // 用户请求中没有token，拒绝请求
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // token存在,校验令牌是否盗用
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        // 从redis中获取令牌
        String redisToken = stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        if (StringUtils.isEmpty(redisToken)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 判断token是否一致
        if (!redisToken.equals(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 正常情况将token放入请求头中
        request.mutate().header("Authorization", "bearer " + token);
        // 放行
        return chain.filter(exchange);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
