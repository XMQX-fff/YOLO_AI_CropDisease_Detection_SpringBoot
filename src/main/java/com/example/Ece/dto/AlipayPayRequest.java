package com.example.Ece.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 支付宝支付请求DTO
 */
@Data
public class AlipayPayRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 支付金额
     */
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 同步回调地址
     */
    private String returnUrl;
}
