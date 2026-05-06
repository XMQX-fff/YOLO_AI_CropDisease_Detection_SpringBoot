package com.example.Ece.controller;

import com.example.Ece.common.Result;
import com.example.Ece.dto.AlipayPayRequest;
import com.example.Ece.dto.AlipayPayResponse;
import com.example.Ece.service.AlipayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 * 处理支付宝支付相关接口
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final AlipayService alipayService;

    /**
     * 创建支付宝支付订单
     *
     * @param request 支付请求
     * @return 支付结果
     */
    @PostMapping("/alipay/create")
    public Result<AlipayPayResponse> createAlipayPayment(@Valid @RequestBody AlipayPayRequest request) {
        log.info("创建支付宝支付订单，订单号: {}", request.getOrderNo());

        try {
            AlipayPayResponse response = alipayService.createPagePay(request);

            if ("PENDING".equals(response.getStatus())) {
                return Result.success(response);
            } else {
                return Result.error(response.getMessage());
            }
        } catch (Exception e) {
            log.error("创建支付宝支付订单异常: {}", e.getMessage(), e);
            return Result.error("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 查询支付宝支付状态
     *
     * @param orderNo 订单号
     * @return 支付状态
     */
    @GetMapping("/alipay/query")
    public Result<AlipayPayResponse> queryAlipayStatus(@RequestParam String orderNo) {
        log.info("查询支付宝支付状态，订单号: {}", orderNo);

        try {
            AlipayPayResponse response = alipayService.queryPayStatus(orderNo);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询支付宝支付状态异常: {}", e.getMessage(), e);
            return Result.error("查询支付状态失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付宝同步回调
     *
     * @param request HTTP请求
     * @return 处理结果
     */
    @GetMapping("/alipay/return")
    public Result<AlipayPayResponse> handleAlipayReturn(HttpServletRequest request) {
        log.info("收到支付宝同步回调");

        // 获取所有回调参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            String valueStr = String.join(",", values);
            params.put(key, valueStr);
        }

        log.debug("支付宝回调参数: {}", params);

        // 验证签名
        boolean signVerified = alipayService.verifyReturn(params);

        if (!signVerified) {
            log.error("支付宝同步回调签名验证失败");
            return Result.error("签名验证失败");
        }

        // 获取交易信息
        String tradeStatus = params.get("trade_status");
        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");

        log.info("支付宝同步回调，订单号: {}，交易号: {}，状态: {}",
                outTradeNo, tradeNo, tradeStatus);

        // 构建响应
        AlipayPayResponse response = AlipayPayResponse.builder()
                .orderNo(outTradeNo)
                .tradeNo(tradeNo)
                .status("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus) ? "paid" : tradeStatus)
                .message("支付回调处理成功")
                .build();

        return Result.success(response);
    }

    /**
     * 关闭支付宝订单
     *
     * @param orderNo 订单号
     * @return 处理结果
     */
    @PostMapping("/alipay/close")
    public Result<Void> closeAlipayOrder(@RequestParam String orderNo) {
        log.info("关闭支付宝订单，订单号: {}", orderNo);
        // TODO: 实现关闭订单逻辑
        return Result.success();
    }

    /**
     * 支付宝退款
     *
     * @param orderNo      订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 处理结果
     */
    @PostMapping("/alipay/refund")
    public Result<Void> refundAlipayPayment(
            @RequestParam String orderNo,
            @RequestParam String refundAmount,
            @RequestParam String refundReason) {
        log.info("支付宝退款，订单号: {}，金额: {}，原因: {}",
                orderNo, refundAmount, refundReason);
        // TODO: 实现退款逻辑
        return Result.success();
    }

    /**
     * 验证支付结果
     *
     * @param orderNo 订单号
     * @param tradeNo 支付宝交易号
     * @return 验证结果
     */
    @PostMapping("/alipay/verify")
    public Result<Boolean> verifyPayment(
            @RequestParam String orderNo,
            @RequestParam String tradeNo) {
        log.info("验证支付结果，订单号: {}，交易号: {}", orderNo, tradeNo);

        try {
            AlipayPayResponse response = alipayService.queryPayStatus(orderNo);
            boolean isSuccess = "paid".equals(response.getStatus()) &&
                    tradeNo.equals(response.getTradeNo());
            return Result.success(isSuccess);
        } catch (Exception e) {
            log.error("验证支付结果异常: {}", e.getMessage(), e);
            return Result.error("验证失败: " + e.getMessage());
        }
    }
}
