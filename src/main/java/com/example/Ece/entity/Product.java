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
 * 商品实体类
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Integer categoryId;

    /**
     * 产品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 副标题
     */
    @TableField("sub_title")
    private String subTitle;

    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 销量
     */
    @TableField("sales_count")
    private Integer salesCount;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Integer supplierId;

    /**
     * 地区
     */
    @TableField("region")
    private String region;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 产品描述
     */
    @TableField("description")
    private String description;

    /**
     * 规格参数（JSON格式）
     */
    @TableField("specifications")
    private String specifications;

    /**
     * 是否上架：0-下架，1-上架
     */
    @TableField("is_on_sale")
    private Boolean isOnSale;

    /**
     * 是否推荐：0-否，1-是
     */
    @TableField("is_recommend")
    private Boolean isRecommend;

    /**
     * 是否新品：0-否，1-是
     */
    @TableField("is_new")
    private Boolean isNew;

    /**
     * 是否热销：0-否，1-是
     */
    @TableField("is_hot")
    private Boolean isHot;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 评分（1-5分）
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 评价数量
     */
    @TableField("review_count")
    private Integer reviewCount;

    /**
     * 配送区域
     */
    @TableField("delivery_area")
    private String deliveryArea;

    /**
     * 运费
     */
    @TableField("delivery_fee")
    private BigDecimal deliveryFee;

    /**
     * 满多少免运费
     */
    @TableField("free_delivery_amount")
    private BigDecimal freeDeliveryAmount;

    /**
     * 重量(kg)
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // ========== 非数据库字段 ==========

    /**
     * 商品图片列表
     */
    @TableField(exist = false)
    private List<ProductImage> images;

    /**
     * 主图URL
     */
    @TableField(exist = false)
    private String mainImage;

    /**
     * 分类名称
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 供应商名称
     */
    @TableField(exist = false)
    private String supplierName;

    /**
     * 是否收藏
     */
    @TableField(exist = false)
    private Boolean isFavorite;

    /**
     * 购物车数量
     */
    @TableField(exist = false)
    private Integer cartQuantity;
}
