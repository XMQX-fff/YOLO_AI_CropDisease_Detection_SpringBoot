package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.ProductFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品收藏Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface ProductFavoriteMapper extends BaseMapper<ProductFavorite> {

    /**
     * 根据用户ID和商品ID查询收藏
     */
    default ProductFavorite selectByUserIdAndProductId(Integer userId, Integer productId) {
        LambdaQueryWrapper<ProductFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavorite::getUserId, userId)
               .eq(ProductFavorite::getProductId, productId);
        return selectOne(wrapper);
    }

    /**
     * 检查用户是否收藏了商品
     */
    default boolean checkFavorite(Integer userId, Integer productId) {
        LambdaQueryWrapper<ProductFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavorite::getUserId, userId)
               .eq(ProductFavorite::getProductId, productId);
        return selectOne(wrapper) != null;
    }

    /**
     * 获取收藏数量
     */
    default long getFavoriteCount(Integer userId) {
        LambdaQueryWrapper<ProductFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavorite::getUserId, userId);
        return selectCount(wrapper);
    }

    /**
     * 获取商品的收藏数量
     */
    default long getProductFavoriteCount(Integer productId) {
        LambdaQueryWrapper<ProductFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavorite::getProductId, productId);
        return selectCount(wrapper);
    }
}
