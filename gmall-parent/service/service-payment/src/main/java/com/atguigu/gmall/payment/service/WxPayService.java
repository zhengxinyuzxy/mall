package com.atguigu.gmall.payment.service;

import java.util.Map;

/**
 * @author zhengxinyu
 * @date 2023/11/18
 */
public interface WxPayService {

    /**
     * 获取微信支付的二维码地址
     * @param orderId
     * @param money
     * @param orderdesc
     * @return
     */
    public Map<String, String> getWxPayUrl(Long orderId, Integer money, String orderdesc);

    /**
     * 微信支付查询订单
     * @param orderId
     * @return
     */
    public Map<String, String> getWxOrderquery(Long orderId);
}
