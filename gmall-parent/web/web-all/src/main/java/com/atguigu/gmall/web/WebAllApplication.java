package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@EnableFeignClients({"com.atguigu.gmall.item.fegin", "com.atguigu.gmall.list.feign"})
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class, args);
    }
}
