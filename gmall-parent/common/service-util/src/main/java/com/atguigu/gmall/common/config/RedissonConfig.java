package com.atguigu.gmall.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * redisson配置信息
 */
@Data
@Configuration
@ConfigurationProperties("spring.redis")
public class RedissonConfig {

    private static String ADDRESS_PREFIX = "redis://";
    private String host;
    private String addresses;
    private String password;
    private String port;
    private int timeout = 3000;
    private int connectionPoolSize = 64;
    private int connectionMinimumIdleSize = 10;
    private int pingConnectionInterval = 60000;

    /**
     * 自动装配
     */
    @Bean
    RedissonClient redissonSingle() {
        Config config = new Config();

        if (StringUtils.isEmpty(host)) {
            throw new RuntimeException("host is  empty");
        }
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(ADDRESS_PREFIX + this.host + ":" + port)
                .setTimeout(this.timeout)
                .setPingConnectionInterval(pingConnectionInterval)
                .setConnectionPoolSize(this.connectionPoolSize)
                .setConnectionMinimumIdleSize(this.connectionMinimumIdleSize);
        if (!StringUtils.isEmpty(this.password)) {
            serverConfig.setPassword(this.password);
        }
        return Redisson.create(config);
    }
}