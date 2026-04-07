package com.example.Ece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Ece.entity.ShoppingCart;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车Service接口
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 获取用户的购物车列表
     */
    List<ShoppingCart> getCartByUserId(Integer userId);

    /**
     * 添加商品到购物车
     */
    boolean addToCart(Integer userId, Integer productId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    boolean updateQuantity(Integer cartId, Integer quantity);

    /**
     * 删除购物车商品
     */
    boolean removeFromCart(Integer cartId);

    /**
     * 批量删除购物车商品
     */
    boolean batchRemove(List<Integer> cartIds);

    /**
     * 清空购物车
     */
    boolean clearCart(Integer userId);

    /**
     * 选中/取消选中商品
     */
    boolean toggleSelected(Integer cartId, Boolean selected);

    /**
     * 全选/取消全选
     */
    boolean toggleAllSelected(Integer userId, Boolean selected);

    /**
     * 获取购物车中选中的商品
     */
    List<ShoppingCart> getSelectedCart(Integer userId);

    /**
     * 获取购物车商品数量
     */
    Long getCartCount(Integer userId);

    /**
     * 计算选中商品的总金额
     */
    BigDecimal calculateTotal(Integer userId);

    /**
     * 删除选中的商品
     */
    boolean removeSelected(Integer userId);
}
