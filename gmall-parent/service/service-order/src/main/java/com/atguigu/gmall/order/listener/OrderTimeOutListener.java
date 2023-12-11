package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author zhengxinyu
 * @date 2023/11/23
 */
@Component
@Log4j2
public class OrderTimeOutListener {

    @Autowired
    private OrderService orderService;

    /**
     * 超时未支付订单的监听类
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "order_queue")
    public void orderTimeOutCancleListener(Channel channel, Message message) {
        // 获取消息内容
        byte[] body = message.getBody();
        String orderIdStr = new String(body);
        long orderId = Long.parseLong(orderIdStr);
        // 获取消息编号
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        try {
            // 取消超时订单
            orderService.cancleOrder(orderId, OrderStatus.TIME_OUT.getComment());
            // 手动确定
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            // 判断消失是否被消费过
            try {
                if (messageProperties.isRedelivered()) {
                    log.error("取消订单失败！订单id为：" + orderId);
                    channel.basicReject(deliveryTag, false);
                }
                // 消费失败，重新返回队列
                channel.basicReject(deliveryTag, true);
            } catch (IOException ex) {
                log.error("取消订单失败！订单id为：" + orderId);
            }
        }
    }

}
