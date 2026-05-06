package com.example.Ece.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置类
 * 配置支付宝沙箱环境参数
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    /**
     * 支付宝应用ID
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String merchantPrivateKey;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;

    /**
     * 页面跳转同步通知页面路径
     */
    private String returnUrl;

    /**
     * 签名方式
     */
    private String signType = "RSA2";

    /**
     * 字符编码格式
     */
    private String charset = "UTF-8";

    /**
     * 支付宝网关
     */
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /**
     * 日志路径
     */
    private String logPath = "/tmp/alipay/log";

    /**
     * 是否沙箱环境
     */
    private boolean sandbox = true;
}
