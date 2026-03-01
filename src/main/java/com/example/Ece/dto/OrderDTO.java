package com.example.Ece.dto;

import com.example.Ece.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDTO extends Order {
    // 物流状态
    private String logisticsStatus;
    // 物流单号
    private String logisticsNo;
    // 物流公司
    private String logisticsCompany;
    
    // 构造函数，从Order转换
    public OrderDTO(Order order) {
        super(order.getId(), order.getOrderNo(), order.getUserId(),
              order.getTotalPrice(), order.getStatus(), order.getAddress(),
              order.getPhone(), order.getRemark(), order.getOrderTime(),
              order.getPaidTime(), order.getShippedTime(), order.getCompletedTime(),
              order.getCancelledTime());
    }
}