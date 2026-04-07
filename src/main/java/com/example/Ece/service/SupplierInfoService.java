package com.example.Ece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Ece.entity.SupplierInfo;

import java.util.List;

/**
 * 供应商信息Service接口
 */
public interface SupplierInfoService extends IService<SupplierInfo> {

    /**
     * 根据用户ID获取供应商信息
     */
    SupplierInfo getSupplierByUserId(Integer userId);

    /**
     * 获取已认证的供应商列表
     */
    List<SupplierInfo> getVerifiedSuppliers();

    /**
     * 更新供应商销量
     */
    boolean increaseSaleCount(Integer supplierId, Integer quantity);

    /**
     * 更新供应商商品数量
     */
    boolean updateProductCount(Integer supplierId, Integer count);
}
