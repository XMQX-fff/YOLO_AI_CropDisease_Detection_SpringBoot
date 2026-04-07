package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.SupplierInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 供应商信息Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface SupplierInfoMapper extends BaseMapper<SupplierInfo> {

    /**
     * 根据用户ID获取供应商信息
     */
    default SupplierInfo selectByUserId(Integer userId) {
        LambdaQueryWrapper<SupplierInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierInfo::getUserId, userId);
        return selectOne(wrapper);
    }

    /**
     * 获取已认证的供应商列表
     */
    default List<SupplierInfo> selectVerifiedSuppliers() {
        LambdaQueryWrapper<SupplierInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierInfo::getIsVerified, true)
               .eq(SupplierInfo::getIsEnabled, true)
               .orderByDesc(SupplierInfo::getRating);
        return selectList(wrapper);
    }

    /**
     * 更新销量
     */
    default int increaseSaleCount(Integer supplierId, Integer quantity) {
        // 使用自定义SQL或者先查询再更新
        SupplierInfo supplier = selectById(supplierId);
        if (supplier != null) {
            supplier.setSaleCount(supplier.getSaleCount() + quantity);
            return updateById(supplier);
        }
        return 0;
    }

    /**
     * 更新商品数量
     */
    default int updateProductCount(Integer supplierId, Integer count) {
        LambdaUpdateWrapper<SupplierInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SupplierInfo::getId, supplierId)
               .set(SupplierInfo::getProductCount, count);
        return update(null, wrapper);
    }
}
