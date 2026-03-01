package com.example.Ece.dto;

import com.example.Ece.entity.PaymentRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentRecordDTO extends PaymentRecord {
    // 支付方式名称
    private String paymentMethodName;
    
    // 构造函数，从PaymentRecord转换
    public PaymentRecordDTO(PaymentRecord paymentRecord) {
        super(paymentRecord.getId(), paymentRecord.getPaymentNo(), paymentRecord.getOrderId(),
              paymentRecord.getAmount(), paymentRecord.getPaymentMethodId(), paymentRecord.getStatus(),
              paymentRecord.getPaymentTime(), paymentRecord.getTransactionNo());
    }
}