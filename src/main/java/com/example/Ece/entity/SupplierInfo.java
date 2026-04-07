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
 * 供应商信息实体类
 */
@Data
@TableName("supplier_info")
public class SupplierInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 公司名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 公司Logo
     */
    @TableField("company_logo")
    private String companyLogo;

    /**
     * 营业执照
     */
    @TableField("business_license")
    private String businessLicense;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField("contact_phone")
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    @TableField("contact_email")
    private String contactEmail;

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
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 经营范围
     */
    @TableField("business_scope")
    private String businessScope;

    /**
     * 公司介绍
     */
    @TableField("company_description")
    private String companyDescription;

    /**
     * 店铺评分
     */
    @TableField("rating")
    private BigDecimal rating;

    /**
     * 累计销量
     */
    @TableField("sale_count")
    private Integer saleCount;

    /**
     * 商品数量
     */
    @TableField("product_count")
    private Integer productCount;

    /**
     * 是否认证：0-否，1-是
     */
    @TableField("is_verified")
    private Boolean isVerified;

    /**
     * 是否启用：0-否，1-是
     */
    @TableField("is_enabled")
    private Boolean isEnabled;

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
     * 用户信息
     */
    @TableField(exist = false)
    private User user;

    /**
     * 完整地址
     */
    @TableField(exist = false)
    private String fullAddress;

    public String getFullAddress() {
        return province + city + district + address;
    }
}
