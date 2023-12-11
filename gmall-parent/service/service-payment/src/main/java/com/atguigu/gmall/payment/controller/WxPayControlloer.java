package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson2.JSONObject;
import com.atguigu.gmall.payment.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengxinyu
 * @date 2023/11/19
 */
@RestController
@RequestMapping(value = "/payment/wxPay")
public class WxPayControlloer {

    @Resource
    private WxPayService wxPayService;

    @GetMapping(value = "/getWxPayCodeUrl")
    public Map<String, String> getWxPayCodeUrl(Long orderId, Integer money, String orderdesc) {
        return wxPayService.getWxPayUrl(orderId, money, orderdesc);
    }

    @GetMapping(value = "/getWxPayOrderquery")
    public Map<String, String> getWxPayOrderquery(Long orderId) {
        return wxPayService.getWxOrderquery(orderId);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/wxPayNotify")
    public String wxPayNotify(HttpServletRequest httpServletRequest) throws Exception {
        ServletInputStream is = httpServletRequest.getInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = is.read(bytes)) != -1) {
            os.write(bytes, 0, len);
        }
        byte[] byteArray = os.toByteArray();
        String stringXml = new String(byteArray, StandardCharsets.UTF_8);
        System.out.println(stringXml);
        Map<String, String> map = WXPayUtil.xmlToMap(stringXml);
        // 发送消息到mq
        rabbitTemplate.convertAndSend(
                "pay_exchange",
                "pay.wx",
                JSONObject.toJSONString(map));
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("return_code", "SUCCESS");
        returnMap.put("return_msg", "OK");
        return WXPayUtil.mapToXml(returnMap);
    }
}
