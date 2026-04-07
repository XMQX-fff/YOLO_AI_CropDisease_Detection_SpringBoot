package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户收货地址Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    /**
     * 获取用户的收货地址列表
     */
    default List<UserAddress> selectByUserId(Integer userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .orderByDesc(UserAddress::getIsDefault)
               .orderByDesc(UserAddress::getId);
        return selectList(wrapper);
    }

    /**
     * 获取用户的默认地址
     */
    default UserAddress selectDefaultByUserId(Integer userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .eq(UserAddress::getIsDefault, true)
               .last("LIMIT 1");
        return selectOne(wrapper);
    }

    /**
     * 取消用户的默认地址
     */
    default int cancelDefault(Integer userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .set(UserAddress::getIsDefault, false);
        return update(null, wrapper);
    }

    /**
     * 设置默认地址
     */
    default int setDefault(Integer addressId) {
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getId, addressId)
               .set(UserAddress::getIsDefault, true);
        return update(null, wrapper);
    }
}
