package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@TableName("payment_record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String paymentNo;
    private Integer orderId;
    private BigDecimal amount;
    private Integer paymentMethodId;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;
    private String transactionNo;
}