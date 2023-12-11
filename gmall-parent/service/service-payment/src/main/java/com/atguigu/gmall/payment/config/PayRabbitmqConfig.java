package com.atguigu.gmall.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhengxinyu
 * @date 2023/11/23
 */
@Configuration
public class PayRabbitmqConfig {

    /**
     * 支付交换机
     * @return
     */
    @Bean("payExchange")
    public Exchange payExchange() {
        return ExchangeBuilder.directExchange("pay_exchange").build();
    }

    /**
     * 微信队列
     * @return
     */
    @Bean("wxQueue")
    public Queue wxQueue() {
        return QueueBuilder.durable("wx_pay_queue").build();
    }

    /**
     * 支付宝队列
     * @return
     */
    @Bean("aliQueue")
    public Queue aliQueue() {
        return QueueBuilder.durable("ali_pay_queue").build();
    }

    /**
     * 交换机和微信队列绑定
     * @param payExchange
     * @param wxQueue
     * @return
     */
    @Bean
    public Binding bindingWxQueue(@Qualifier("payExchange") Exchange payExchange,
                                  @Qualifier("wxQueue") Queue wxQueue) {
        return BindingBuilder.bind(wxQueue).to(payExchange).with("pay.wx").noargs();
    }

    /**
     * 交换机和支付宝队列绑定
     * @param payExchange
     * @param aliQueue
     * @return
     */
    @Bean
    public Binding bindingAliQueue(@Qualifier("payExchange") Exchange payExchange,
                                  @Qualifier("aliQueue") Queue aliQueue) {
        return BindingBuilder.bind(aliQueue).to(payExchange).with("pay.ali").noargs();
    }

}
