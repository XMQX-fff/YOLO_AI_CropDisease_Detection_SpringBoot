package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价实体类
 */
@Data
@TableName("product_review")
public class ProductReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单ID
     */
    @TableField("order_id")
    private Integer orderId;

    /**
     * 订单商品ID
     */
    @TableField("order_item_id")
    private Integer orderItemId;

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
     * 评分（1-5分）
     */
    @TableField("rating")
    private Integer rating;

    /**
     * 评价内容
     */
    @TableField("content")
    private String content;

    /**
     * 评价图片（JSON数组）
     */
    @TableField("images")
    private String images;

    /**
     * 是否匿名：0-否，1-是
     */
    @TableField("is_anonymous")
    private Boolean isAnonymous;

    /**
     * 商家回复
     */
    @TableField("reply_content")
    private String replyContent;

    /**
     * 回复时间
     */
    @TableField("reply_time")
    private LocalDateTime replyTime;

    /**
     * 是否显示：0-否，1-是
     */
    @TableField("is_show")
    private Boolean isShow;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    // ========== 非数据库字段 ==========

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private User user;

    /**
     * 商品信息
     */
    @TableField(exist = false)
    private Product product;

    /**
     * 评价图片列表
     */
    @TableField(exist = false)
    private java.util.List<String> imageList;
}
