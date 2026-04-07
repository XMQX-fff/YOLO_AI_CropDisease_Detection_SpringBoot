package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 收货地址ID
     */
    @TableField("address_id")
    private Integer addressId;

    /**
     * 订单总价
     */
    @TableField("total_price")
    private BigDecimal totalPrice;

    /**
     * 运费
     */
    @TableField("delivery_fee")
    private BigDecimal deliveryFee;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 支付方式
     */
    @TableField("pay_method")
    private String payMethod;

    /**
     * 订单状态：pending-待支付、paid-已支付、shipped-已发货、completed-已完成、cancelled-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 收货地址
     */
    @TableField("address")
    private String address;

    /**
     * 收货人姓名
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 收货人电话
     */
    @TableField("receiver_phone")
    private String receiverPhone;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区县
     */
    @TableField("district")
    private String district;

    /**
     * 配送方式
     */
    @TableField("delivery_method")
    private String deliveryMethod;

    /**
     * 快递单号
     */
    @TableField("tracking_no")
    private String trackingNo;

    /**
     * 物流公司
     */
    @TableField("logistics_company")
    private String logisticsCompany;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 买家留言
     */
    @TableField("buyer_message")
    private String buyerMessage;

    /**
     * 下单时间
     */
    @TableField("order_time")
    private LocalDateTime orderTime;

    /**
     * 支付时间
     */
    @TableField("paid_time")
    private LocalDateTime paidTime;

    /**
     * 发货时间
     */
    @TableField("shipped_time")
    private LocalDateTime shippedTime;

    /**
     * 完成时间
     */
    @TableField("completed_time")
    private LocalDateTime completedTime;

    /**
     * 取消时间
     */
    @TableField("cancelled_time")
    private LocalDateTime cancelledTime;

    /**
     * 取消原因
     */
    @TableField("cancel_reason")
    private String cancelReason;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    // ========== 非数据库字段 ==========

    /**
     * 订单商品列表
     */
    @TableField(exist = false)
    private List<OrderItem> orderItems;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private User user;

    /**
     * 状态文本
     */
    @TableField(exist = false)
    private String statusText;

    public String getStatusText() {
        if (status == null) return "";
        switch (status) {
            case "pending": return "待支付";
            case "paid": return "已支付";
            case "shipped": return "已发货";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default: return status;
        }
    }
}
