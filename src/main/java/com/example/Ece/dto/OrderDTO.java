package com.example.Ece.dto;

import com.example.Ece.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        // 复制所有父类属性
        this.setId(order.getId());
        this.setOrderNo(order.getOrderNo());
        this.setUserId(order.getUserId());
        this.setAddressId(order.getAddressId());
        this.setTotalPrice(order.getTotalPrice());
        this.setDeliveryFee(order.getDeliveryFee());
        this.setDiscountAmount(order.getDiscountAmount());
        this.setPayAmount(order.getPayAmount());
        this.setPayMethod(order.getPayMethod());
        this.setStatus(order.getStatus());
        this.setAddress(order.getAddress());
        this.setReceiverName(order.getReceiverName());
        this.setReceiverPhone(order.getReceiverPhone());
        this.setProvince(order.getProvince());
        this.setCity(order.getCity());
        this.setDistrict(order.getDistrict());
        this.setDeliveryMethod(order.getDeliveryMethod());
        this.setTrackingNo(order.getTrackingNo());
        this.setLogisticsCompany(order.getLogisticsCompany());
        this.setRemark(order.getRemark());
        this.setBuyerMessage(order.getBuyerMessage());
        this.setOrderTime(order.getOrderTime());
        this.setPaidTime(order.getPaidTime());
        this.setShippedTime(order.getShippedTime());
        this.setCompletedTime(order.getCompletedTime());
        this.setCancelledTime(order.getCancelledTime());
        this.setCancelReason(order.getCancelReason());
        this.setCreateTime(order.getCreateTime());
        this.setUpdateTime(order.getUpdateTime());
    }
}
