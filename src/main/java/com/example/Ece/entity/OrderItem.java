package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单商品实体类
 */
@Data
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 产品ID
     */
    @TableField("product_id")
    private Integer productId;

    /**
     * 商品名称（快照）
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品图片（快照）
     */
    @TableField("product_image")
    private String productImage;

    /**
     * 规格信息
     */
    @TableField("specifications")
    private String specifications;

    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 购买单价
     */
    @TableField("price")
    private BigDecimal price;

    // ========== 非数据库字段 ==========

    /**
     * 小计金额
     */
    @TableField(exist = false)
    private BigDecimal subtotal;

    public BigDecimal getSubtotal() {
        if (price != null && quantity != null) {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    /**
     * 商品信息
     */
    @TableField(exist = false)
    private Product product;
}
