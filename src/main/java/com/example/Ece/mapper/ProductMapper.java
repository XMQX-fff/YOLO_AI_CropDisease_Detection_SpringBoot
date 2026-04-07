package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 获取推荐商品
     */
    default List<Product> selectRecommendProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getIsRecommend, true)
               .eq(Product::getIsOnSale, true)
               .orderByDesc(Product::getId)
               .last("LIMIT " + limit);
        return selectList(wrapper);
    }

    /**
     * 获取热销商品
     */
    default List<Product> selectHotProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getIsHot, true)
               .eq(Product::getIsOnSale, true)
               .orderByDesc(Product::getSalesCount)
               .last("LIMIT " + limit);
        return selectList(wrapper);
    }

    /**
     * 获取新品
     */
    default List<Product> selectNewProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getIsNew, true)
               .eq(Product::getIsOnSale, true)
               .orderByDesc(Product::getCreatedAt)
               .last("LIMIT " + limit);
        return selectList(wrapper);
    }
}
