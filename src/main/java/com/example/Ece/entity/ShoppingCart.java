package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体类
 */
@Data
@TableName("shopping_cart")
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Integer productId;

    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 是否选中：0-否，1-是
     */
    @TableField("selected")
    private Boolean selected;

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
     * 商品信息
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 小计金额
     */
    @TableField(exist = false)
    private BigDecimal subtotal;

    /**
     * 是否有效（库存充足）
     */
    @TableField(exist = false)
    private Boolean isValid;

    /**
     * 无效原因
     */
    @TableField(exist = false)
    private String invalidReason;
}
