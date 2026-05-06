package com.example.Ece.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.example.Ece.config.AlipayConfig;
import com.example.Ece.dto.AlipayPayRequest;
import com.example.Ece.dto.AlipayPayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 支付宝支付服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlipayService {

    private final AlipayConfig alipayConfig;

    private AlipayClient alipayClient;

    @PostConstruct
    public void init() {
        // 初始化支付宝客户端
        this.alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getMerchantPrivateKey(),
                "json",
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType()
        );
        log.info("支付宝客户端初始化完成，沙箱环境: {}", alipayConfig.isSandbox());
    }

    /**
     * 创建电脑网站支付订单
     *
     * @param request 支付请求
     * @return 支付响应
     */
    public AlipayPayResponse createPagePay(AlipayPayRequest request) {
        try {
            // 创建API请求
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

            // 设置同步回调地址
            alipayRequest.setReturnUrl(request.getReturnUrl() != null ?
                    request.getReturnUrl() : alipayConfig.getReturnUrl());

            // 构建请求参数
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(request.getOrderNo());
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            model.setTotalAmount(request.getAmount().toString());
            model.setSubject(request.getSubject() != null ?
                    request.getSubject() : "订单支付");
            model.setBody(request.getBody());
            model.setTimeoutExpress("30m");

            alipayRequest.setBizModel(model);

            // 调用SDK生成表单
            String form = alipayClient.pageExecute(alipayRequest).getBody();

            log.info("创建支付宝支付订单成功，订单号: {}", request.getOrderNo());

            return AlipayPayResponse.builder()
                    .payUrl(form)
                    .orderNo(request.getOrderNo())
                    .status("PENDING")
                    .message("创建支付订单成功")
                    .build();

        } catch (AlipayApiException e) {
            log.error("创建支付宝支付订单失败，订单号: {}，错误: {}",
                    request.getOrderNo(), e.getMessage(), e);
            return AlipayPayResponse.builder()
                    .orderNo(request.getOrderNo())
                    .status("FAILED")
                    .message("创建支付订单失败: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 查询订单支付状态
     *
     * @param orderNo 订单号
     * @return 支付响应
     */
    public AlipayPayResponse queryPayStatus(String orderNo) {
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderNo);

            request.setBizModel(model);

            AlipayTradeQueryResponse response = alipayClient.execute(request);

            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                String status = convertTradeStatus(tradeStatus);

                log.info("查询支付宝订单状态成功，订单号: {}，状态: {}",
                        orderNo, tradeStatus);

                return AlipayPayResponse.builder()
                        .orderNo(orderNo)
                        .tradeNo(response.getTradeNo())
                        .status(status)
                        .message("查询成功")
                        .build();
            } else {
                log.warn("查询支付宝订单状态失败，订单号: {}，错误码: {}，错误信息: {}",
                        orderNo, response.getCode(), response.getMsg());

                return AlipayPayResponse.builder()
                        .orderNo(orderNo)
                        .status("FAILED")
                        .message(response.getMsg())
                        .build();
            }

        } catch (AlipayApiException e) {
            log.error("查询支付宝订单状态异常，订单号: {}，错误: {}",
                    orderNo, e.getMessage(), e);
            return AlipayPayResponse.builder()
                    .orderNo(orderNo)
                    .status("ERROR")
                    .message("查询异常: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 验证支付宝同步回调签名
     *
     * @param params 回调参数
     * @return 验证结果
     */
    public boolean verifyReturn(Map<String, String> params) {
        try {
            return AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType()
            );
        } catch (AlipayApiException e) {
            log.error("验证支付宝回调签名失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 转换支付宝交易状态
     *
     * @param tradeStatus 支付宝交易状态
     * @return 系统状态
     */
    private String convertTradeStatus(String tradeStatus) {
        if (tradeStatus == null) {
            return "UNKNOWN";
        }

        switch (tradeStatus) {
            case "TRADE_SUCCESS":
            case "TRADE_FINISHED":
                return "paid";
            case "WAIT_BUYER_PAY":
                return "pending";
            case "TRADE_CLOSED":
                return "closed";
            default:
                return "unknown";
        }
    }
}
