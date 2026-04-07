package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品图片Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {

    /**
     * 根据商品ID获取图片列表
     */
    default List<ProductImage> selectByProductId(Integer productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId)
               .orderByDesc(ProductImage::getIsMain)
               .orderByAsc(ProductImage::getSortOrder);
        return selectList(wrapper);
    }

    /**
     * 获取商品主图
     */
    default ProductImage selectMainImage(Integer productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId)
               .eq(ProductImage::getIsMain, true)
               .last("LIMIT 1");
        return selectOne(wrapper);
    }

    /**
     * 根据商品ID删除所有图片
     */
    default void deleteByProductId(Integer productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId);
        delete(wrapper);
    }
}
