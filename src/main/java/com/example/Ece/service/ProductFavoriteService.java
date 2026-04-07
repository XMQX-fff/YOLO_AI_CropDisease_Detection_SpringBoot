package com.example.Ece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Ece.entity.ProductFavorite;

import java.util.List;

/**
 * 商品收藏Service接口
 */
public interface ProductFavoriteService extends IService<ProductFavorite> {

    /**
     * 添加收藏
     */
    boolean addFavorite(Integer userId, Integer productId);

    /**
     * 取消收藏
     */
    boolean removeFavorite(Integer userId, Integer productId);

    /**
     * 获取用户的收藏列表
     */
    List<ProductFavorite> getFavoritesByUserId(Integer userId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorite(Integer userId, Integer productId);

    /**
     * 切换收藏状态
     */
    boolean toggleFavorite(Integer userId, Integer productId);

    /**
     * 获取收藏数量
     */
    Long getFavoriteCount(Integer userId);
}
