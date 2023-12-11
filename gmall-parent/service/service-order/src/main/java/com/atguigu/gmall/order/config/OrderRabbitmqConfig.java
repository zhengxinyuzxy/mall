package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 超时订单相关的队列和交换机的创建配置
 * @author zhengxinyu
 * @date 2023/11/23
 */
@Configuration
public class OrderRabbitmqConfig {
    // 正常交换机
    @Bean("orderExchange")
    public Exchange orderExchange() {
        return ExchangeBuilder.directExchange("order_exchange").build();
    }

    // 死信队列,转发规则
    @Bean("delayQueue")
    public Queue delayQueue() {
        return QueueBuilder
                .durable("delay_queue")
                .withArgument("x-dead-letter-exchange", "delay_exchange")
                .withArgument("x-dead-letter-routing-key", "order.cancle")
                .build();
    }

    // 正常交换机和死信队列的绑定
    @Bean
    public Binding orderExchangeBangDingDelayQueue(@Qualifier("orderExchange") Exchange orderExchange,
                                                   @Qualifier("delayQueue") Queue delayQueue) {
        return BindingBuilder.bind(delayQueue).to(orderExchange).with("order.delay").noargs();
    }

    // 死信交换机
    @Bean("delayExchange")
    public Exchange delayExchange() {
        return ExchangeBuilder.directExchange("delay_exchange").build();
    }

    // 正常队列
    @Bean("orderQueue")
    public Queue orderQueue() {
        return QueueBuilder.durable("order_queue").build();
    }

    // 死信交换机和正常队列的绑定
    @Bean
    public Binding delayExchangeBangDingOrderQueue(@Qualifier("delayExchange") Exchange delayExchange,
                                                   @Qualifier("orderQueue") Queue orderQueue) {
        return BindingBuilder.bind(orderQueue).to(delayExchange).with("order.cancle").noargs();
    }
}
