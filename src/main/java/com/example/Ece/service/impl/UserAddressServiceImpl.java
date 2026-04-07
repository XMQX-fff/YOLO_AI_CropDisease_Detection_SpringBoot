package com.example.Ece.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Ece.entity.UserAddress;
import com.example.Ece.mapper.UserAddressMapper;
import com.example.Ece.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户收货地址Service实现类
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public List<UserAddress> getAddressByUserId(Integer userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public UserAddress getDefaultAddress(Integer userId) {
        return baseMapper.selectDefaultByUserId(userId);
    }

    @Override
    @Transactional
    public boolean addAddress(UserAddress address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            baseMapper.cancelDefault(address.getUserId());
        }
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        return save(address);
    }

    @Override
    @Transactional
    public boolean updateAddress(UserAddress address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            UserAddress oldAddress = getById(address.getId());
            if (oldAddress != null) {
                baseMapper.cancelDefault(oldAddress.getUserId());
            }
        }
        address.setUpdateTime(LocalDateTime.now());
        return updateById(address);
    }

    @Override
    public boolean deleteAddress(Integer addressId) {
        return removeById(addressId);
    }

    @Override
    @Transactional
    public boolean setDefaultAddress(Integer userId, Integer addressId) {
        // 取消其他默认地址
        baseMapper.cancelDefault(userId);
        // 设置新的默认地址
        baseMapper.setDefault(addressId);
        return true;
    }

    @Override
    public UserAddress getAddressById(Integer addressId) {
        return getById(addressId);
    }
}
