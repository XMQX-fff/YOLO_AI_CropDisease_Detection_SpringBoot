package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品收藏实体类
 */
@Data
@TableName("product_favorite")
public class ProductFavorite implements Serializable {

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
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    // ========== 非数据库字段 ==========

    /**
     * 商品信息
     */
    @TableField(exist = false)
    private Product product;
}
