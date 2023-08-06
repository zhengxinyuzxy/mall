package com.atguigu.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 商品管理微服务的启动类
 */

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall") // 指定包扫描，开启自己指定的功能
@EnableFeignClients("com.atguigu.gmall.list.feign")
public class ProductApplication {

    /**
     * springboot启动类的工作流程原理一：
     * 1.1 起步依赖，在SSM中所有的jar包都需要自己管理，现在springboot自动管理所有你可能使用到的版本号
     * 1.2 简单理解为父工程管理所有版本号，依赖里引入了所有你可能使用的坐标，依赖的传递性
     * springboot启动类的工作流程原理二：
     * 2.1 自动装配/自动配置
     * 2.2 SpringApplication.run生成容器，传入启动类字节码文件装入容器中，可以理解为构建spring的ioc容器
     * 2.2.1 由BeanFactory创建容器，FactoryBean存储管理容器
     * 2.3 加载SpringBootApplication注解,包含@ComponentScan/@SpringBootConfiguration/@EnableAutoConfiguration注解
     * 2.4 @ComponentScan注解，包扫描，启动类所在包下的所有类和所有子包中所有类的所有注解-自定义的bean交给ioc容器管理
     * 2.5 @SpringBootConfiguration注解，标注当前启动类是一个配置类
     * 2.6 @EnableAutoConfiguration注解， 加载所有引入坐标的容器对象完成对象的初始化实例化
     * 2.6.1 @Import({AutoConfigurationImportSelector.class})动态导入/动态加载，类加载才会动态加载AutoConfigurationImportSelector.class进来
     * Selector（选择器）,项目启动是加载spring.factories里的内容，使用反射机制完成实例化，其中不抛异常把对象加载完成放入容器中。引入坐标则初始化实例化完成
     * 先读取配置文件，完成初始化
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
