package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.ListService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author zhengxinyu
 * @date 2023/11/23
 */
@Log4j2
public class ListListener {

    @Autowired
    private ListService listService;

    @RabbitListener(queues = "sku_up_queue")
    public void skuUpperListener(Channel channel, Message message) {
        updateSkuInfoStatus(channel, message, 1);
    }

    @RabbitListener(queues = "sku_down_queue")
    public void skuDownListener(Channel channel, Message message) {
        updateSkuInfoStatus(channel, message, 0);
    }

    private void updateSkuInfoStatus(Channel channel, Message message, Integer status) {
        byte[] body = message.getBody();
        String skuIdString = new String(body);
        MessageProperties messageProperties = message.getMessageProperties();
        // 获取签名编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            // 修改上下架
            if (status ==1) {
                // 上架
                listService.upper(Long.parseLong(skuIdString));
            } else {
                // 下架
                listService.down(Long.parseLong(skuIdString));
            }
            // 手动确认，不批量
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                //确认消息是否被消费过
                if (messageProperties.isRedelivered()) {
                    log.error("商品同步失败,商品的id为:" + skuIdString);
                    channel.basicReject(deliveryTag, false);
                    return;
                }
                // 重新放入队列，再试一次
                channel.basicReject(deliveryTag, true);
            } catch (IOException ex) {
                log.error("商品同步失败,商品的id为:" + skuIdString);
            }
        }
    }
}
