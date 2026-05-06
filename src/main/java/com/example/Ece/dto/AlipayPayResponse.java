package com.example.Ece.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 支付宝支付响应DTO
 */
@Data
@Builder
public class AlipayPayResponse {

    /**
     * 支付页面URL
     */
    private String payUrl;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 支付状态
     */
    private String status;

    /**
     * 消息
     */
    private String message;
}
