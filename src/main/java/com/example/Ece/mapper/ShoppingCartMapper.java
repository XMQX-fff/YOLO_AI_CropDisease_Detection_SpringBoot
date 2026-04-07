package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 购物车Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    /**
     * 根据用户ID查询购物车列表
     */
    default List<ShoppingCart> selectByUserId(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
               .orderByDesc(ShoppingCart::getCreateTime);
        return selectList(wrapper);
    }

    /**
     * 根据用户ID和商品ID查询购物车
     */
    default ShoppingCart selectByUserIdAndProductId(Integer userId, Integer productId) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
               .eq(ShoppingCart::getProductId, productId);
        return selectOne(wrapper);
    }

    /**
     * 获取购物车中选中的商品
     */
    default List<ShoppingCart> selectSelectedCartByUserId(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
               .eq(ShoppingCart::getSelected, true);
        return selectList(wrapper);
    }

    /**
     * 全选/取消全选
     */
    default int updateAllSelected(Integer userId, Boolean selected) {
        ShoppingCart cart = new ShoppingCart();
        cart.setSelected(selected);

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        return update(cart, wrapper);
    }

    /**
     * 清空购物车
     */
    default void clearCart(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        delete(wrapper);
    }

    /**
     * 删除选中的商品
     */
    default void deleteSelected(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId)
               .eq(ShoppingCart::getSelected, true);
        delete(wrapper);
    }

    /**
     * 获取购物车商品数量
     */
    default long getCartCount(Integer userId) {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        return selectCount(wrapper);
    }
}
