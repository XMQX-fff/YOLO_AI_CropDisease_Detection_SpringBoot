package com.example.Ece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Ece.entity.UserAddress;

import java.util.List;

/**
 * 用户收货地址Service接口
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取用户的收货地址列表
     */
    List<UserAddress> getAddressByUserId(Integer userId);

    /**
     * 获取用户的默认地址
     */
    UserAddress getDefaultAddress(Integer userId);

    /**
     * 添加收货地址
     */
    boolean addAddress(UserAddress address);

    /**
     * 更新收货地址
     */
    boolean updateAddress(UserAddress address);

    /**
     * 删除收货地址
     */
    boolean deleteAddress(Integer addressId);

    /**
     * 设置默认地址
     */
    boolean setDefaultAddress(Integer userId, Integer addressId);

    /**
     * 获取地址详情
     */
    UserAddress getAddressById(Integer addressId);
}
