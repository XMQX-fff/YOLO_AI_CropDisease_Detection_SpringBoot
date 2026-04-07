package com.example.Ece.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Ece.entity.SupplierInfo;
import com.example.Ece.mapper.SupplierInfoMapper;
import com.example.Ece.service.SupplierInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 供应商信息Service实现类
 */
@Service
public class SupplierInfoServiceImpl extends ServiceImpl<SupplierInfoMapper, SupplierInfo> implements SupplierInfoService {

    @Override
    public SupplierInfo getSupplierByUserId(Integer userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<SupplierInfo> getVerifiedSuppliers() {
        return baseMapper.selectVerifiedSuppliers();
    }

    @Override
    public boolean increaseSaleCount(Integer supplierId, Integer quantity) {
        return baseMapper.increaseSaleCount(supplierId, quantity) > 0;
    }

    @Override
    public boolean updateProductCount(Integer supplierId, Integer count) {
        return baseMapper.updateProductCount(supplierId, count) > 0;
    }
}
