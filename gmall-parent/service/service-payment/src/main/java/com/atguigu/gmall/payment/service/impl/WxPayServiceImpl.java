package com.atguigu.gmall.payment.service.impl;

import com.atguigu.gmall.payment.service.WxPayService;
import com.atguigu.gmall.payment.util.HttpClient;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengxinyu
 * @date 2023/11/18
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Value("${weixin.pay.appid}")
    private String appid;

    @Value("${weixin.pay.partner}")
    private String partner;

    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;


    /**
     * 获取微信支付的二维码地址
     * @param orderId
     * @param money
     * @param orderdesc
     * @return
     */
    @Override
    public Map<String, String> getWxPayUrl(Long orderId, Integer money, String orderdesc) {
        // 参数校验
        if (orderId == null || money == null || StringUtils.isEmpty(orderdesc)) {
            return null;
        }
        // 包装请求参数，并转换为xml格式
        Map<String, String> paramsMap = new HashMap<>();
        // 公众账号ID
        paramsMap.put("appid", appid);
        // 商户号
        paramsMap.put("mch_id", partner);
        // 随机字符串
        paramsMap.put("nonce_str", WXPayUtil.generateNonceStr());
        // 商品描述
        paramsMap.put("body", orderdesc);
        // 商户订单号
        paramsMap.put("out_trade_no", String.valueOf(orderId));
        // 标价金额
        paramsMap.put("total_fee", String.valueOf(money));
        // 通知地址
        paramsMap.put("notify_url", notifyUrl);
        // 交易类型
        paramsMap.put("trade_type", "NATIVE");
        try {
            String paramsXml = WXPayUtil.generateSignedXml(paramsMap, partnerkey);
            // 发送post请求，请求微信的支付服务
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramsXml);
            // 发起请求
            httpClient.post();
            // 获取结果
            String content = httpClient.getContent();
            // 解析xml格式的结果
            Map<String, String> xmlMap = WXPayUtil.xmlToMap(content);
            if ("SUCCESS".equals(xmlMap.get("return_code"))
                    && "SUCCESS".equals(xmlMap.get("result_code"))) {
                // String codeUrl = xmlMap.get("code_url");
                return xmlMap;
            }
            return xmlMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getWxOrderquery(Long orderId) {
        if (orderId == null) {
            return null;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("appid", appid);
        paramsMap.put("mch_id", partner);
        paramsMap.put("out_trade_no", orderId + "");
        paramsMap.put("nonce_str", WXPayUtil.generateNonceStr());
        // 包装请求参数，转换为xml格式
        try {
            String paramsXml = WXPayUtil.generateSignedXml(paramsMap, partnerkey);
            // 发送post请求，请求微信的查询订单服务
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramsXml);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            if ("SUCCESS".equals(map.get("return_code"))
                    && "SUCCESS".equals(map.get("result_code"))
                    && "SUCCESS".equals(map.get("trade_state"))) {
                map.put("交易成功", "交易成功");
                return map;
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
