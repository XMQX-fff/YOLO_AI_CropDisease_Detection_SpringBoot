package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品分类Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {

    /**
     * 获取所有显示的分类
     */
    default List<ProductCategory> selectAllShowCategories() {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getIsShow, true)
               .orderByAsc(ProductCategory::getSortOrder)
               .orderByAsc(ProductCategory::getId);
        return selectList(wrapper);
    }

    /**
     * 根据父ID获取子分类
     */
    default List<ProductCategory> selectByParentId(Integer parentId) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategory::getParentId, parentId)
               .eq(ProductCategory::getIsShow, true)
               .orderByAsc(ProductCategory::getSortOrder);
        return selectList(wrapper);
    }
}
