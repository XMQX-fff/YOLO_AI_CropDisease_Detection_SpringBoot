package com.example.Ece.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Ece.entity.Product;
import com.example.Ece.entity.ProductFavorite;
import com.example.Ece.entity.ProductImage;
import com.example.Ece.mapper.ProductFavoriteMapper;
import com.example.Ece.mapper.ProductImageMapper;
import com.example.Ece.mapper.ProductMapper;
import com.example.Ece.service.ProductFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品收藏Service实现类
 */
@Service
public class ProductFavoriteServiceImpl extends ServiceImpl<ProductFavoriteMapper, ProductFavorite> implements ProductFavoriteService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Override
    public boolean addFavorite(Integer userId, Integer productId) {
        // 检查是否已收藏
        if (isFavorite(userId, productId)) {
            return true;
        }

        ProductFavorite favorite = new ProductFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favorite.setCreateTime(LocalDateTime.now());
        return save(favorite);
    }

    @Override
    public boolean removeFavorite(Integer userId, Integer productId) {
        ProductFavorite favorite = baseMapper.selectByUserIdAndProductId(userId, productId);
        if (favorite != null) {
            return removeById(favorite.getId());
        }
        return true;
    }

    @Override
    public List<ProductFavorite> getFavoritesByUserId(Integer userId) {
        // 使用MyBatis-Plus的list方法
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductFavorite> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(ProductFavorite::getUserId, userId)
               .orderByDesc(ProductFavorite::getCreateTime);
        List<ProductFavorite> favorites = list(wrapper);
        
        // 加载商品信息
        for (ProductFavorite favorite : favorites) {
            Product product = productMapper.selectById(favorite.getProductId());
            if (product != null) {
                // 加载商品主图
                ProductImage mainImage = productImageMapper.selectMainImage(product.getId());
                if (mainImage != null) {
                    product.setMainImage(mainImage.getImageUrl());
                }
                favorite.setProduct(product);
            }
        }
        return favorites;
    }

    @Override
    public boolean isFavorite(Integer userId, Integer productId) {
        return baseMapper.checkFavorite(userId, productId);
    }

    @Override
    public boolean toggleFavorite(Integer userId, Integer productId) {
        if (isFavorite(userId, productId)) {
            return removeFavorite(userId, productId);
        } else {
            return addFavorite(userId, productId);
        }
    }

    @Override
    public Long getFavoriteCount(Integer userId) {
        return baseMapper.getFavoriteCount(userId);
    }
}
